package repository;

import exceptions.InsufficientFundsException;
import model.Account;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.Map;

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
        return account;
    };

    @Override
    public void createAccount(Long userId, BigDecimal moneyAmount) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        String sql = "INSERT INTO accounts(user_id, money_amount) VALUES (:userId, :moneyAmount)";
        Map<String, Object> params = Map.of("userId", userId, "moneyAmount", moneyAmount);
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
        account.setBalance(moneyAmount);
    }



    @Override
    public boolean accountExistByUserId(Long accountId){
        if (accountId == null) {
            throw new IllegalArgumentException("accountUserId cannot be null");
        }
        String sql = "SELECT COUNT(*) FROM accounts WHERE id = :accountId";
        Map<String, Object> params = Map.of("accountUserId", accountId);
        Long count = jdbcTemplate.queryForObject(sql, params, Long.class);
        return count == 0;
    }

    private void validateAccount(Long accountUserId, BigDecimal amount){
        if (accountUserId == null) {
            throw new IllegalArgumentException("accountId cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be negative or null");
        }
    }

    @Override
    public void topUpAccount(Long accountId, BigDecimal amount) {
        validateAccount(accountId, amount);

        if (accountExistByUserId(accountId)){
            throw new IllegalArgumentException("Account with id " + accountId + " doesn't exists");
        }

        String sql = "UPDATE accounts SET money_amount = money_amount + :amount WHERE id = :accountId";
        Map<String, Object> params = Map.of("accountId", accountId, "amount", amount);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void withdrawAccount(Long accountId, BigDecimal amount) {
        validateAccount(accountId, amount);

        if (accountExistByUserId(accountId)){
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
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {

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
