package app.dao;

import app.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    @Query("""
       SELECT a FROM Activity a
       WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))
       """)
    List<Activity> searchByTitle(@Param("title") String title);

}
