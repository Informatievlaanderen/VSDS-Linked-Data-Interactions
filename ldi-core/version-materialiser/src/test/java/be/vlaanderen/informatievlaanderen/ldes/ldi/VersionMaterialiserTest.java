/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionMaterialiserTest {

	@Test
	void testVersionMaterialiseMemberOnly() throws IOException {
		Model initModel = ModelFactory.createDefaultModel();

		VersionMaterialiser versionMaterialiser = new VersionMaterialiser(
				initModel.createProperty("http://purl.org/dc/terms/isVersionOf"), true);

		Model versionedMember = turtleFileToQuadString("src/test/resources/ldes-member-versioned.ttl");

		Model output = versionMaterialiser.transform(versionedMember);

		InputStream comparisonFile = new FileInputStream("src/test/resources/ldes-member-unversioned.ttl");
		Model comparisonModel = RDFParser.create()
				.source(comparisonFile)
				.lang(Lang.TURTLE)
				.toModel();

		assertTrue(output.isIsomorphicWith(comparisonModel));
	}

	@Test
	void testVersionMaterialiseWithContext() throws IOException {
		Model initModel = ModelFactory.createDefaultModel();

		VersionMaterialiser versionMaterialiser = new VersionMaterialiser(
				initModel.createProperty("http://purl.org/dc/terms/isVersionOf"), false);

		Model versionedMember = turtleFileToQuadString("src/test/resources/ldes-member-versioned.ttl");

		Model output = versionMaterialiser.transform(versionedMember);

		InputStream comparisonFile = new FileInputStream(
				"src/test/resources/ldes-member-unversioned-context-included.ttl");
		Model comparisonModel = RDFParser.create()
				.source(comparisonFile)
				.lang(Lang.TURTLE)
				.toModel();

		assertTrue(output.isIsomorphicWith(comparisonModel));
	}

	private Model turtleFileToQuadString(String filename) {
		try {
			Path filePath = Path.of(filename);
			// Processor needs nquads for now.
			String versionedInput = Files.readString(filePath);
			InputStream versionedInputStream = IOUtils.toInputStream(versionedInput);
			return RDFParser.create()
					.source(versionedInputStream)
					.lang(Lang.TURTLE)
					.toModel();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
