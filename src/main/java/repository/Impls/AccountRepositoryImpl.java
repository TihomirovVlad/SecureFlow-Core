package repository.Impls;

import exceptions.InsufficientFundsException;
import model.Account;
import model.enums.AccountStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AccountRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = namedParameterJdbcTemplate;
    }

    private static final RowMapper<Account> ACCOUNT_ROW_MAPPER = (rs, rowNum) -> {
        Account account = new Account();
        account.setId(rs.getLong("id"));
        account.setUserId(rs.getLong("user_id"));
        account.setBalance(rs.getBigDecimal("money_amount"));
        account.setAccountStatus(AccountStatus.valueOf(rs.getString("status")));
        return account;
    };

    @Override
    public Account createAccount(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        String sql = "INSERT INTO accounts(user_id, status) VALUES (:userId, 'ACTIVE')";
        Map<String, Object> params = Map.of("userId", userId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                sql,
                new MapSqlParameterSource(params),
                keyHolder,
                new String[]{"id"}
        );
        Long id = keyHolder.getKey().longValue();
        Account account = new Account();
        account.setId(id);
        account.setUserId(userId);
        return account;
    }

    @Override
    public void updateAccountStatus(Long accountId, AccountStatus status) {
        if (accountId == null || status == null) {
            throw new IllegalArgumentException("AccountId and status cannot be null");
        }

        String sql = "UPDATE accounts SET status = :status WHERE id = :accountId";
        Map<String, Object> params = Map.of("accountId", accountId, "status", status.name());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public Optional<Account> getAccountById(Long accountId){
        if (accountId == null) {
            throw new IllegalArgumentException("accountId cannot be null");
        }
        String sql = "SELECT * FROM accounts WHERE id = :accountId";
        Map<String, Object> params = Map.of("accountId", accountId);
        try {
            Account account = jdbcTemplate.queryForObject(sql, params, ACCOUNT_ROW_MAPPER);
            return Optional.ofNullable(account);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Account> findAccountsByUserId(Long userId){
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        String sql = "SELECT * FROM accounts WHERE user_id = :userId";
        Map<String, Object> params = Map.of("userId", userId);
        return jdbcTemplate.query(sql, params, ACCOUNT_ROW_MAPPER);
    }

    private boolean accountDoesNotExist(Long accountId){
        if (accountId == null) {
            throw new IllegalArgumentException("accountUserId cannot be null");
        }
        String sql = "SELECT COUNT(*) FROM accounts WHERE id = :accountId";
        Map<String, Object> params = Map.of("accountId", accountId);
        Long count = jdbcTemplate.queryForObject(sql, params, Long.class);
        return count <= 0;
    }

    private void validateAccount(Long accountId, BigDecimal amount){
        if (accountId == null) {
            throw new IllegalArgumentException("accountId cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be negative or null");
        }
    }

    @Override
    public void topUpAccount(Long accountId, BigDecimal amount) {
        validateAccount(accountId, amount);

        if (accountDoesNotExist(accountId)){
            throw new IllegalArgumentException("Account with id " + accountId + " doesn't exists");
        }

        String sql = "UPDATE accounts SET money_amount = money_amount + :amount WHERE id = :accountId";
        Map<String, Object> params = Map.of("accountId", accountId, "amount", amount);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void withdrawAccount(Long accountId, BigDecimal amount) {
        validateAccount(accountId, amount);

        if (accountDoesNotExist(accountId)){
            throw new IllegalArgumentException("Account with id " + accountId + " doesn't exists");
        }

        String sql = """
                UPDATE accounts
                SET money_amount = money_amount - :amount
                WHERE id = :accountId and money_amount >= :amount
                """;
        Map<String, Object> params = Map.of(
                "accountId", accountId,
                "amount", amount
        );
        int rows = jdbcTemplate.update(sql, new MapSqlParameterSource(params));
        if (rows == 0) throw new InsufficientFundsException("Not enough funds in account with ID: " + accountId);

    }

    @Override
    public void deleteAccount(Long accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("accountId cannot be null");
        }
        String sql = "DELETE FROM accounts WHERE id = :accountId";
        Map<String, Object> params = Map.of("accountId", accountId);
        jdbcTemplate.update(sql, params);
    }
}
