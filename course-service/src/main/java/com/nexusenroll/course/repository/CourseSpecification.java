package com.nexusenroll.course.repository;

import com.nexusenroll.course.dto.CourseSearchFilterDTO;
import com.nexusenroll.course.model.Course;
import com.nexusenroll.course.model.CourseSection;
import com.nexusenroll.course.model.Department;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for dynamic, multi-criteria course searching matching old behavior.
 */
public class CourseSpecification {

    public static Specification<Course> filter(CourseSearchFilterDTO filter) {
        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            // Join Department for code / name filtering
            Root<Department> deptRoot = query.from(Department.class);
            predicates.add(cb.equal(root.get("departmentId"), deptRoot.get("id")));

            // Filter keyword across courseCode, title, description, department code, department name
            if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
                String pattern = "%" + filter.getKeyword().trim().toLowerCase() + "%";
                Predicate courseCodeLike = cb.like(cb.lower(root.get("courseCode")), pattern);
                Predicate titleLike = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), pattern);
                Predicate deptCodeLike = cb.like(cb.lower(deptRoot.get("code")), pattern);
                Predicate deptNameLike = cb.like(cb.lower(deptRoot.get("name")), pattern);

                predicates.add(cb.or(courseCodeLike, titleLike, descLike, deptCodeLike, deptNameLike));
            }

            // Department code exact match
            if (filter.getDepartmentCode() != null && !filter.getDepartmentCode().isBlank()) {
                predicates.add(cb.equal(cb.lower(deptRoot.get("code")), filter.getDepartmentCode().trim().toLowerCase()));
            }

            // Course number exact match
            if (filter.getCourseNumber() != null && filter.getCourseNumber() > 0) {
                predicates.add(cb.equal(root.get("courseNumber"), filter.getCourseNumber()));
            }

            // Section level filters (instructor, semester, year)
            boolean needsSectionFilter = (filter.getInstructorId() != null && filter.getInstructorId() > 0)
                    || (filter.getSemester() != null && !filter.getSemester().isBlank())
                    || (filter.getYear() != null && filter.getYear() > 0);

            if (needsSectionFilter) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<CourseSection> sectionRoot = subquery.from(CourseSection.class);
                List<Predicate> subPredicates = new ArrayList<>();
                subPredicates.add(cb.equal(sectionRoot.get("courseId"), root.get("id")));

                if (filter.getInstructorId() != null && filter.getInstructorId() > 0) {
                    subPredicates.add(cb.equal(sectionRoot.get("instructorId"), filter.getInstructorId()));
                }
                if (filter.getSemester() != null && !filter.getSemester().isBlank()) {
                    subPredicates.add(cb.equal(cb.lower(sectionRoot.get("semester")), filter.getSemester().trim().toLowerCase()));
                }
                if (filter.getYear() != null && filter.getYear() > 0) {
                    subPredicates.add(cb.equal(sectionRoot.get("year"), filter.getYear()));
                }

                subquery.select(sectionRoot.get("courseId")).where(subPredicates.toArray(new Predicate[0]));
                predicates.add(cb.in(root.get("id")).value(subquery));
            }

            query.orderBy(cb.asc(root.get("title")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
