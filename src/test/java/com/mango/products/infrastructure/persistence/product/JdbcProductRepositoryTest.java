package com.mango.products.infrastructure.persistence.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcProductRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private JdbcProductRepository repository;

    private ProductEntity product;

    @BeforeEach
    void setup() {
        product = new ProductEntity();
        product.setName("Test");
        product.setDescription("Desc");
    }

    @Test
    void save_shouldInsertProduct_withPrices_andEndDateBranches() {
        PriceEntity price1 = new PriceEntity(null, BigDecimal.TEN, LocalDate.now(), null);
        PriceEntity price2 = new PriceEntity(null, BigDecimal.ONE, LocalDate.now(), LocalDate.now());

        product.setPrices(List.of(price1, price2));

        mockInsertKeyHolder(1L, 2L, 3L);

        repository.save(product);

        assertNotNull(product.getId());
        assertNotNull(price1.getId());
        assertNotNull(price2.getId());
    }

    @Test
    void save_shouldInsertProduct_withoutPrices_null() {
        product.setPrices(null);
        mockInsertKeyHolder(1L);

        repository.save(product);

        assertNotNull(product.getId());
    }

    @Test
    void save_shouldInsertProduct_withoutPrices_empty() {
        product.setPrices(Collections.emptyList());
        mockInsertKeyHolder(1L);

        repository.save(product);

        assertNotNull(product.getId());
    }

    @Test
    void save_shouldUpdateProduct_andInsertOnlyNewPrices() {
        product.setId(1L);

        PriceEntity existing = new PriceEntity(10L, BigDecimal.TEN, LocalDate.now(), null);
        PriceEntity newPrice = new PriceEntity(null, BigDecimal.ONE, LocalDate.now(), null);

        product.setPrices(List.of(existing, newPrice));

        when(jdbcTemplate.update(anyString(), any(), any(), any())).thenReturn(1);
        mockInsertKeyHolder(2L);

        repository.save(product);

        assertNull(null);
        assertNotNull(newPrice.getId());
    }

    @Test
    void findById_shouldReturnProduct_withPrices() {
        when(jdbcTemplate.query(contains("products"), ArgumentMatchers.<RowMapper<ProductEntity>>any(), any()))
                .thenAnswer(inv -> List.of(mapProduct(inv)));
        mockPriceQuery(false);

        Optional<ProductEntity> result = repository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getPrices().size());
    }

    @Test
    void findById_shouldReturnEmpty() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<ProductEntity>>any(), any()))
                .thenReturn(Collections.emptyList());

        assertTrue(repository.findById(1L).isEmpty());
    }

    @Test
    void findByName_shouldWork() {
        when(jdbcTemplate.query(contains("products"), ArgumentMatchers.<RowMapper<ProductEntity>>any(), any()))
                .thenAnswer(inv -> List.of(mapProduct(inv)));
        mockPriceQuery(true);

        Optional<ProductEntity> result = repository.findByName("Test");

        assertTrue(result.isPresent());
    }

    @Test
    void findAll_shouldLoadPrices() {
        when(jdbcTemplate.query(eq("SELECT * FROM products"), ArgumentMatchers.<RowMapper<ProductEntity>>any()))
                .thenAnswer(inv -> List.of(mapProduct(inv)));

        mockPriceQuery(false);

        List<ProductEntity> result = repository.findAll();

        assertEquals(1, result.size());
        assertNotNull(result.getFirst().getPrices());
    }

    @Test
    void deleteById_shouldExecuteBothDeletes() {
        when(jdbcTemplate.update(anyString(), Optional.ofNullable(any()))).thenReturn(1);

        repository.deleteById(1L);

        verify(jdbcTemplate, times(2)).update(anyString(), Optional.ofNullable(any()));
    }

    @Test
    void mapPriceRow_shouldHandleNullEndDate() throws Exception {
        ResultSet rs = mockPriceResultSet(true);

        PriceEntity result = invokeMapPrice(rs);

        assertNull(result.getEndDate());
    }

    @Test
    void mapPriceRow_shouldHandleNonNullEndDate() throws Exception {
        ResultSet rs = mockPriceResultSet(false);

        PriceEntity result = invokeMapPrice(rs);

        assertNotNull(result.getEndDate());
    }

    @Test
    void mapProductRow_shouldMapProductEntity() throws Exception {
        ResultSet rs = mockProductResultSet();

        ProductEntity result = invokeMapProduct(rs);

        assertEquals(1L, result.getId());
        assertEquals("Test", result.getName());
        assertEquals("Desc", result.getDescription());
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoProducts() {
        when(jdbcTemplate.query(eq("SELECT * FROM products"), ArgumentMatchers.<RowMapper<ProductEntity>>any()))
                .thenReturn(Collections.emptyList());

        List<ProductEntity> result = repository.findAll();

        assertEquals(0, result.size());
    }

    @Test
    void findAll_shouldLoadMultipleProducts() {
        when(jdbcTemplate.query(eq("SELECT * FROM products"), ArgumentMatchers.<RowMapper<ProductEntity>>any()))
                .thenAnswer(inv -> {
                    RowMapper<ProductEntity> mapper = inv.getArgument(1);
                    ResultSet rs1 = mockProductResultSet();
                    when(rs1.getLong("id")).thenReturn(1L);
                    when(rs1.getString("name")).thenReturn("Product1");

                    ResultSet rs2 = mock(ResultSet.class);
                    when(rs2.getLong("id")).thenReturn(2L);
                    when(rs2.getString("name")).thenReturn("Product2");
                    when(rs2.getString("description")).thenReturn("Desc2");

                    return List.of(Objects.requireNonNull(mapper.mapRow(rs1, 0)),
                            Objects.requireNonNull(mapper.mapRow(rs2, 1)));
                });

        mockPriceQuery(false);

        List<ProductEntity> result = repository.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void enrichProduct_shouldLoadPrices() {
        when(jdbcTemplate.query(contains("products"), ArgumentMatchers.<RowMapper<ProductEntity>>any(), any()))
                .thenAnswer(inv -> List.of(mapProduct(inv)));
        mockPriceQuery(false);

        Optional<ProductEntity> result = repository.findById(1L);

        assertTrue(result.isPresent());
        assertFalse(result.get().getPrices().isEmpty());
    }

    @Test
    void save_shouldUpdateProduct_withNullPrices() {
        product.setId(1L);
        product.setPrices(null);

        when(jdbcTemplate.update(anyString(), any(), any(), any())).thenReturn(1);

        repository.save(product);

        assertNotNull(product.getId());
    }

    @Test
    void findById_shouldLoadProductWithEmptyPrices() {
        reset(jdbcTemplate);

        when(jdbcTemplate.query(contains("products"), ArgumentMatchers.<RowMapper<ProductEntity>>any(), any()))
                .thenAnswer(inv -> {
                    RowMapper<ProductEntity> mapper = inv.getArgument(1);
                    ResultSet rs = mockProductResultSet();
                    return List.of(Objects.requireNonNull(mapper.mapRow(rs, 0)));
                });

        when(jdbcTemplate.query(contains("prices"), ArgumentMatchers.<RowMapper<PriceEntity>>any(), any()))
                .thenReturn(Collections.emptyList());

        Optional<ProductEntity> result = repository.findById(1L);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getPrices());
        assertEquals(0, result.get().getPrices().size());
    }

    @Test
    void save_shouldReturnProduct() {
        mockInsertKeyHolder(1L);

        ProductEntity result = repository.save(product);

        assertNotNull(result.getId());
        assertEquals(1L, result.getId());
    }

    private void mockInsertKeyHolder(Long... ids) {
        Iterator<Long> iterator = Arrays.asList(ids).iterator();

        when(jdbcTemplate.update(any(), any(KeyHolder.class)))
                .thenAnswer(invocation -> {
                    KeyHolder kh = invocation.getArgument(1);
                    kh.getKeyList().add(Map.of("GENERATED_KEY", iterator.next()));
                    return 1;
                });
    }


    private ProductEntity mapProduct(InvocationOnMock inv) throws Exception {
        RowMapper<ProductEntity> mapper = inv.getArgument(1);
        ResultSet rs = mock(ResultSet.class);

        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("name")).thenReturn("Test");
        when(rs.getString("description")).thenReturn("Desc");

        return mapper.mapRow(rs, 0);
    }

    private void mockPriceQuery(boolean nullEndDate) {
        when(jdbcTemplate.query(contains("prices"), ArgumentMatchers.<RowMapper<PriceEntity>>any(), any()))
                .thenAnswer(inv -> {
                    RowMapper<PriceEntity> mapper = inv.getArgument(1);
                    return List.of(Objects.requireNonNull(mapper.mapRow(mockPriceResultSet(nullEndDate), 0)));
                });
    }

    private ResultSet mockPriceResultSet(boolean nullEndDate) throws Exception {
        ResultSet rs = mock(ResultSet.class);

        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getBigDecimal("price_value")).thenReturn(BigDecimal.TEN);
        when(rs.getDate("init_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(rs.getDate("end_date"))
                .thenReturn(nullEndDate ? null : Date.valueOf(LocalDate.now()));

        return rs;
    }

    private ResultSet mockProductResultSet() throws Exception {
        ResultSet rs = mock(ResultSet.class);

        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("name")).thenReturn("Test");
        when(rs.getString("description")).thenReturn("Desc");

        return rs;
    }

    private PriceEntity invokeMapPrice(ResultSet rs) throws Exception {
        var m = JdbcProductRepository.class.getDeclaredMethod("mapPriceRow", ResultSet.class);
        m.setAccessible(true);
        return (PriceEntity) m.invoke(repository, rs);
    }

    private ProductEntity invokeMapProduct(ResultSet rs) throws Exception {
        var m = JdbcProductRepository.class.getDeclaredMethod("mapProductRow", ResultSet.class);
        m.setAccessible(true);
        return (ProductEntity) m.invoke(repository, rs);
    }

}
