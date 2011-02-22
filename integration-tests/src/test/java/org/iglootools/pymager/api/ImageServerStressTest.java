/**
 * Copyright 2009 Sami Dalouche
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iglootools.pymager.api;

import static org.hamcrest.CoreMatchers.is;
import static org.iglootools.pymager.api.ImageFormat.JPEG;
import static org.iglootools.pymager.api.ImageReference.originalImage;
import static org.iglootools.pymager.api.ImageScale.width;
import static org.iglootools.pymager.api.testhelpers.ImageIdObjectMother.cornicheKabyleId;
import static org.iglootools.pymager.api.testhelpers.ImageIdObjectMother.yemmaGourayaId;
import static org.iglootools.pymager.api.testhelpers.ImageReferenceObjectMother.cornicheKabyle;
import static org.iglootools.pymager.api.testhelpers.ImageReferenceObjectMother.yemmaGouraya;
import static org.iglootools.pymager.api.testhelpers.PictureStreamAssertionUtils.isCornicheKabylePicture;
import static org.iglootools.pymager.api.testhelpers.PictureStreamAssertionUtils.isYemmaGourayaPicture;
import static org.iglootools.pymager.api.testhelpers.PictureStreamSourceObjectMother.yemmaGourayaDerived100x100PictureStream;
import static org.iglootools.pymager.api.testhelpers.PictureStreamSourceObjectMother.yemmaGourayaOriginalPictureStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.iglootools.pymager.api.ImageReference;
import org.iglootools.pymager.api.ResourceNotExistingException;
import org.iglootools.pymager.api.jobs.DownloadPictureJob;
import org.iglootools.pymager.api.jobs.OperationStatus;
import org.iglootools.pymager.api.jobs.UploadPictureJob;
import org.iglootools.pymager.api.testhelpers.PictureStreamSourceObjectMother;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.InputSupplier;

public class ImageServerStressTest extends AbstractImageServerIntegrationTestCase {
    private final static long TIMEOUT_IN_SECONDS = 500;
    private final static int THREAD_POOL_SIZE = 50;
    private final static int TOTAL_NUMBER_OF_THREADS = 500;
    private ExecutorService executorService;

    @Before
    public void setup() throws IOException {
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        initialFailproofCleanup();
    }

    private void initialFailproofCleanup() throws IOException {
        for (ImageReference imageReference : Lists.newArrayList(yemmaGouraya(),cornicheKabyle())) {
            try {
                InputSupplier<InputStream> source = imageServer.downloadImage(imageReference);
                source.getInput();
                imageServer.deleteImage(imageReference.getId());
            } catch (ResourceNotExistingException e) {
                // do nothing, it's fine
            }
        }
    }

    @Test
    public void onlyOneUploadThreadOfYemmaGourayaShoudSucceed() throws InterruptedException, IOException {
        Iterable<Future<OperationStatus>> results = executorService.invokeAll(
                uploadYemmaGourayaJobs(TOTAL_NUMBER_OF_THREADS),
                TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        assertThat(numberOfSuccessfulOperations(results), is(1));
        assertThat(numberOfUnsuccessfulOperations(results),is(TOTAL_NUMBER_OF_THREADS - 1));

        InputSupplier<InputStream> source = imageServer.downloadImage(yemmaGouraya());
        assertNotNull(source);
        assertTrue(isYemmaGourayaPicture(source));
        imageServer.deleteImage(yemmaGourayaId());
    }

    @Test
    public void onlyOneUploadThreadOfEachImageIdShoudSucceed() throws InterruptedException, IOException {
        Iterable<Future<OperationStatus>> results = executorService
                .invokeAll(
                        Lists
                                .newArrayList(Iterables
                                        .concat(
                                                uploadYemmaGourayaJobs(TOTAL_NUMBER_OF_THREADS / 2),
                                                uploadCornicheKabyleJobs(TOTAL_NUMBER_OF_THREADS / 2))),
                        TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        assertThat(numberOfSuccessfulOperations(results), is(2));
        assertThat(numberOfUnsuccessfulOperations(results),is(TOTAL_NUMBER_OF_THREADS - 2));

        InputSupplier<InputStream> yemmaGourayaSource = imageServer.downloadImage(yemmaGouraya());
        assertNotNull(yemmaGourayaSource);
        assertTrue(isYemmaGourayaPicture(yemmaGourayaSource));

        InputSupplier<InputStream> cornicheKabyleSource = imageServer.downloadImage(cornicheKabyle());
        assertNotNull(cornicheKabyleSource);
        assertTrue(isCornicheKabylePicture(cornicheKabyleSource));

        imageServer.deleteImage(yemmaGourayaId());
        imageServer.deleteImage(cornicheKabyleId());
    }

    @Test
    public void shouldUploadAndDownloadOriginalYemmaGourayaPictureUsingSeveralThreads() throws IOException, InterruptedException {
        uploadYemmaGourayaPicture();

        Iterable<Future<OperationStatus>> results = executorService.invokeAll(
                downloadYemmaGourayaJobs(TOTAL_NUMBER_OF_THREADS),
                TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        assertTrue(allOperationsAreSuccessful(results));
        imageServer.deleteImage(yemmaGourayaId());
    }

    @Test
    public void shouldUploadAndDownloadDerivedYemmaGourayaPictureUsingSeveralThreads() throws IOException, InterruptedException {
        uploadYemmaGourayaPicture();

        Iterable<Future<OperationStatus>> results = executorService.invokeAll(
                downloadYemmaGouraya100x100Jobs(TOTAL_NUMBER_OF_THREADS),
                TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        assertTrue(allOperationsAreSuccessful(results));
        imageServer.deleteImage(yemmaGourayaId());
    }

    @Test
    public void shouldCorrectlyReleaseConnectionsWhenExceptionsAreThrown()
            throws IOException {
        for (int i = 0; i < 100; i++) {
            ImageReference imageReference = originalImage("anyImageThatNobodyHasEverUploadedOnThisPlanet");
            try {
                InputSupplier<InputStream> source = imageServer.downloadImage(imageReference);
                source.getInput();
                fail();
            } catch (ResourceNotExistingException e) {
                assertEquals(imageReference, e.getImageReference());
            }
        }
    }

    private void uploadYemmaGourayaPicture() {
        imageServer.uploadImage(yemmaGourayaId(), JPEG,
                yemmaGourayaOriginalPictureStream());
    }

    private int numberOfUnsuccessfulOperations(
            Iterable<Future<OperationStatus>> results) {
        return Iterables.frequency(Iterables.transform(results,
                extractOperationStatusFunction()), OperationStatus.KO);
    }

    private int numberOfSuccessfulOperations(
            Iterable<Future<OperationStatus>> results) {
        return Iterables.frequency(Iterables.transform(results,
                extractOperationStatusFunction()), OperationStatus.OK);
    }

    private boolean allOperationsAreSuccessful(
            Iterable<Future<OperationStatus>> results) {
        return Iterables.all(Iterables.transform(results,
                extractOperationStatusFunction()),
                new Predicate<OperationStatus>() {
                    public boolean apply(OperationStatus operationStatus) {
                        return OperationStatus.OK.equals(operationStatus);
                    }
                });
    }

    private Function<Future<OperationStatus>, OperationStatus> extractOperationStatusFunction() {
        return new Function<Future<OperationStatus>, OperationStatus>() {
            public OperationStatus apply(Future<OperationStatus> arg0) {
                try {
                    return arg0.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private List<Callable<OperationStatus>> downloadYemmaGouraya100x100Jobs(
            int numberOfThreads) {
        List<Callable<OperationStatus>> jobs = new ArrayList<Callable<OperationStatus>>();
        for (int i = 0; i < numberOfThreads; i++) {
            jobs.add(new DownloadPictureJob(imageServer, yemmaGouraya().rescaledTo(width(100).by(100)),yemmaGourayaDerived100x100PictureStream()));
        }
        return jobs;
    }

    private List<Callable<OperationStatus>> downloadYemmaGourayaJobs(
            int numberOfThreads) {
        List<Callable<OperationStatus>> jobs = new ArrayList<Callable<OperationStatus>>();
        for (int i = 0; i < numberOfThreads; i++) {
            jobs.add(new DownloadPictureJob(imageServer, yemmaGouraya(),
                    yemmaGourayaOriginalPictureStream()));
        }
        return jobs;
    }

    private List<Callable<OperationStatus>> uploadYemmaGourayaJobs(
            int numberOfThreads) {
        List<Callable<OperationStatus>> jobs = new ArrayList<Callable<OperationStatus>>();
        for (int i = 0; i < numberOfThreads; i++) {
            jobs.add(new UploadPictureJob(imageServer, yemmaGourayaId(),
                    yemmaGourayaOriginalPictureStream()));
        }
        return jobs;
    }

    private List<Callable<OperationStatus>> uploadCornicheKabyleJobs(
            int numberOfThreads) {
        List<Callable<OperationStatus>> jobs = new ArrayList<Callable<OperationStatus>>();
        for (int i = 0; i < numberOfThreads; i++) {
            jobs.add(new UploadPictureJob(imageServer, cornicheKabyleId(),
                    PictureStreamSourceObjectMother
                            .cornicheKabyleOriginalPictureStream()));
        }
        return jobs;
    }

}
