package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineConfigRepository extends JpaRepository<PipelineConfigEntity, String> {
}
