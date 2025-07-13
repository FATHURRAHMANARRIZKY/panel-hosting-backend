package f21.my.id.panel_hosting_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import f21.my.id.panel_hosting_backend.model.DeployLog;

public interface DeployLogRepository extends MongoRepository<DeployLog, String>  {

}
