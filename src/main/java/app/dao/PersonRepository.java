package app.dao;

import app.model.Person;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("""
        SELECT p FROM Person p
        WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Person> searchByName(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
        SELECT DISTINCT a.person FROM Activity a
        WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Person> searchByActivity(@Param("keyword") String keyword, Pageable pageable);

    Optional<Person> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    Page<Person> findAll(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Person p " +
            "LEFT JOIN p.cv c " +
            "WHERE LOWER(p.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Person> searchGlobal(@Param("keyword") String keyword, Pageable pageable);
}
