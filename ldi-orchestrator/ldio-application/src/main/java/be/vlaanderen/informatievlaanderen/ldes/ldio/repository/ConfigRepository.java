package be.vlaanderen.informatievlaanderen.ldes.ldio.repository;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig;
import org.springframework.data.repository.CrudRepository;

public interface ConfigRepository extends CrudRepository<PipelineConfig, String> {
}
