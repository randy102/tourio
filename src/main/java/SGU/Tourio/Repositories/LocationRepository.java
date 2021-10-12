package SGU.Tourio.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import SGU.Tourio.Models.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

}