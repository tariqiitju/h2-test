package com.tariqweb.h2test;

import com.tariqweb.h2test.db.entity.People;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class H2testApplicationTests {

	private static final Logger logger = LoggerFactory.getLogger(H2testApplicationTests.class);
	@Autowired
    private TestService testService;
	@Autowired
	EntityManager em;
	@Autowired
    DataSource dataSource;
	@Test
	void contextLoads() {
	}

	@Test
	@Order(1)
	@Transactional
	@Commit
	void insertData() {
		People people = new People();
        people.setName("John Doe");
        people.setBirthDate(Date.valueOf("1990-01-01"));
        people.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
        people.setMemberSince(new java.sql.Timestamp(883612800000L));

//		private OffsetDateTime lastUpdated;
//		@Column(nullable = false)
//		private LocalDateTime lastUpdatedLocal;
//		@Column(nullable = false)
//		private LocalDate lastUpdatedLocalDate;
		people.setLastUpdated(OffsetDateTime.now());
		people.setLastUpdatedLocal(people.getLastUpdated().toLocalDateTime());
		people.setLastUpdatedLocalDate(people.getLastUpdatedLocal().toLocalDate());
        em.persist(people);  // Commit transaction
        logger.info("Inserted data successfully");
	}

	@Test void readData() {
		People people = em.find(People.class, 1L);
        logger.info("Read data: {}", people);
	}
	@Test void readAllData() {
		List<People> peopleList = em.createQuery("SELECT p FROM People p", People.class).getResultList();
        logger.info("Read all data: {}", peopleList);
	}

	@Test
	@Order(2)
	@Transactional
	@Commit
	void setGlobalAlias() {
//		ChronoUnit.DAYS.between()
//		em.createNativeQuery("CREATE ALIAS DATEDIFFX FOR \"java.time.temporal.ChronoUnit.DAYS.between\"").executeUpdate();
		em.createNativeQuery("CREATE ALIAS IF NOT EXISTS REMOVE_DATE_DIFF FOR  \"com.tariqweb.h2test.utils.H2Utils.removeDateDifference\"").executeUpdate();
		em.createNativeQuery("CALL REMOVE_DATE_DIFF();").executeUpdate();
		em.createNativeQuery("DROP ALIAS IF EXISTS DATEDIFF;").executeUpdate();
		em.createNativeQuery("CREATE ALIAS DATEDIFF FOR \"com.tariqweb.h2test.utils.H2Utils.daysBetween\"").executeUpdate();
//		em.createNativeQuery("CREATE ALIAS DATE FOR \"com.tariqweb.h2test.utils.H2Utils.convertStringToSqlDate\"").executeUpdate();
	}

	@Test void nativeQuery() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		List<Map<String, Object>> peopleList = jdbcTemplate.queryForList(
				"""
	SELECT name,
	DATE(birth_date),
	Date(create_time),
	Date(member_since),
	Date(last_updated),
	Date(last_updated_local),
	Date(last_updated_local_date),
	DATEDIFF(member_since, create_time) as days_since_joining,
	DATEDIFF(birth_date, create_time) as days_since_joining2,
	DATEDIFF(last_updated, birth_date) as days_since_joining3,
	DATEDIFF(last_updated_local, birth_date) as days_since_joining4,
	DATEDIFF(last_updated_local_date, birth_date) as days_since_joining5
	FROM people
	WHERE id = 1
	"""
		);
        logger.info("Read native query: {}", peopleList);
	}

	@Test void nativeQueryDate() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		List<Map<String, Object>> peopleList = jdbcTemplate.queryForList(
				"""
	SELECT name,
	(birth_date),
	(create_time),
	(member_since),
	(last_updated),
	(last_updated_local),
	(last_updated_local_date),
	DATEDIFFX(member_since, create_time) as days_since_joining
	FROM people
	WHERE id = 1
	"""
		);
		logger.info("Read native query: {}", peopleList);
	}

}
