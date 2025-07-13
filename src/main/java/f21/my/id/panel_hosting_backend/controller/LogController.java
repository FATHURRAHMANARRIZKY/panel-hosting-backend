package f21.my.id.panel_hosting_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;

import f21.my.id.panel_hosting_backend.model.DeployLog;
import f21.my.id.panel_hosting_backend.repository.DeployLogRepository;

@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private DeployLogRepository logRepo;

    @GetMapping
    public List<DeployLog> getAll() {
        return logRepo.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }
}

