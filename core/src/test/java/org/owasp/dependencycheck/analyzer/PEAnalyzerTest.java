/*
 * This file is part of dependency-check-core.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2020 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.analyzer;

import java.io.File;
import org.junit.After;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.analyzer.exception.UnexpectedAnalysisException;
import org.owasp.dependencycheck.dependency.Confidence;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.dependency.Evidence;
import org.owasp.dependencycheck.dependency.EvidenceType;

/**
 * Tests for the PEAnalyzer.
 *
 * @author Jeremy Long
 *
 */
public class PEAnalyzerTest extends BaseTest {

    private PEAnalyzer analyzer;

    /**
     * Sets up the analyzer.
     *
     * @throws Exception if anything goes sideways
     */
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        analyzer = new PEAnalyzer();
        analyzer.initialize(getSettings());
        analyzer.accept(new File("test.dll")); // trick into "thinking it is active"
        analyzer.prepare(null);
    }

    /**
     * Tests to make sure the name is correct.
     */
    @Test
    public void testGetName() {
        assertEquals("PE Analyzer", analyzer.getName());
    }

    @Test
    public void testAnalysis() throws Exception {
        File f = BaseTest.getResourceAsFile(this, "log4net.dll");

        Dependency d = new Dependency(f);
        analyzer.analyze(d, null);
        assertTrue(d.contains(EvidenceType.VERSION, new Evidence("PE Header", "FileVersion", "1.2.13.0", Confidence.HIGH)));
        assertEquals("1.2.13.0", d.getVersion());
        assertTrue(d.contains(EvidenceType.VENDOR, new Evidence("PE Header", "CompanyName", "The Apache Software Foundation", Confidence.HIGHEST)));
        assertTrue(d.contains(EvidenceType.PRODUCT, new Evidence("PE Header", "ProductName", "log4net", Confidence.HIGHEST)));
        assertEquals("log4net", d.getName());
    }

    @Test
    public void testExeAnalysis() throws AnalysisException {
        // Given
        File file = BaseTest.getResourceAsFile(this, "wildfly-service.exe");

        Dependency dependency = new Dependency(file);

        // When
        analyzer.analyze(dependency, null);

        // Then
    }

    @After
    @Override
    public void tearDown() throws Exception {
        try {
            analyzer.closeAnalyzer();
        } catch (Exception ex) {
            throw new UnexpectedAnalysisException(ex);
        } finally {
            super.tearDown();
        }
    }
}
