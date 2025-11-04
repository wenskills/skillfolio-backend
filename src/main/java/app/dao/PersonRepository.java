package app.dao;

import app.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("""
       SELECT p FROM Person p
       WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
       """)
    List<Person> searchByKeyword(@Param("keyword") String keyword);
}
