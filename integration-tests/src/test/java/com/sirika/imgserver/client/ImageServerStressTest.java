/**
 * ImgServer Java REST Client
 * Copyright (C) 2008 Sami Dalouche
 *
 * This file is part of ImgServer Java REST Client.
 *
 * ImgServer Java REST Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ImgServer Java REST Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ImgServer.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sirika.imgserver.client;

import static com.sirika.imgserver.client.ImageFormat.JPEG;
import static com.sirika.imgserver.client.ImageReference.originalImage;
import static com.sirika.imgserver.client.ImageScale.width;
import static com.sirika.imgserver.client.objectmothers.ImageIdObjectMother.cornicheKabyleId;
import static com.sirika.imgserver.client.objectmothers.ImageIdObjectMother.yemmaGourayaId;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.cornicheKabyle;
import static com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother.yemmaGouraya;
import static com.sirika.imgserver.client.objectmothers.PictureStreamAssertionUtils.is100x100CornicheKabylePicture;
import static com.sirika.imgserver.client.objectmothers.PictureStreamAssertionUtils.isCornicheKabylePicture;
import static com.sirika.imgserver.client.objectmothers.PictureStreamAssertionUtils.isYemmaGourayaPicture;
import static com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother.yemmaGourayaDerived100x100PictureStream;
import static com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother.yemmaGourayaOriginalPictureStream;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.InputStreamSource;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sirika.imgserver.client.jobs.DownloadPictureJob;
import com.sirika.imgserver.client.jobs.OperationStatus;
import com.sirika.imgserver.client.jobs.UploadPictureJob;
import com.sirika.imgserver.client.objectmothers.ImageIdObjectMother;
import com.sirika.imgserver.client.objectmothers.ImageReferenceObjectMother;
import com.sirika.imgserver.client.objectmothers.PictureStreamSourceObjectMother;

public class ImageServerStressTest extends AbstractImageServerIntegrationTestCase {
    private final static long TIMEOUT_IN_SECONDS = 120;
    private final static int THREAD_POOL_SIZE = 50;
    private final static int TOTAL_NUMBER_OF_THREADS = 50;
    private ExecutorService executorService;
    
    @Before
    public void setup() throws IOException {
	executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	initialFailproofCleanup();
    }

    private void initialFailproofCleanup() throws IOException {
	for(ImageReference imageReference : Arrays.asList(yemmaGouraya(), cornicheKabyle())) {
	    try {
		InputStreamSource source = imageServer.downloadImage(imageReference);
		source.getInputStream();
		imageServer.deleteImage(imageReference.getId());
	    } catch(ResourceNotExistingException e) {
		// do nothing, it's fine
	    }
	}
    }
    
    @Test
    public void onlyOneUploadThreadOfYemmaGourayaShoudSucceed() throws InterruptedException, IOException{
	Iterable<Future<OperationStatus>> results = executorService.invokeAll(uploadYemmaGourayaJobs(TOTAL_NUMBER_OF_THREADS), TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
	assertThat(numberOfSuccessfulOperations(results), is(1));
	assertThat(numberOfUnsuccessfulOperations(results), is(TOTAL_NUMBER_OF_THREADS-1));
	
	InputStreamSource source = imageServer.downloadImage(yemmaGouraya());
	assertNotNull(source);
	assertTrue(isYemmaGourayaPicture(source));
	imageServer.deleteImage(yemmaGourayaId());
    }
    
    @Test
    public void onlyOneUploadThreadOfEachImageIdShoudSucceed() throws InterruptedException, IOException{
	Iterable<Future<OperationStatus>> results = 
	    executorService.invokeAll(
		    Lists.newArrayList(
			    Iterables.concat(
				    uploadYemmaGourayaJobs(TOTAL_NUMBER_OF_THREADS/2), 
				    uploadCornicheKabyleJobs(TOTAL_NUMBER_OF_THREADS / 2))), 
		    TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
	assertThat(numberOfSuccessfulOperations(results), is(2));
	assertThat(numberOfUnsuccessfulOperations(results), is(TOTAL_NUMBER_OF_THREADS-2));
	
	InputStreamSource yemmaGourayaSource = imageServer.downloadImage(yemmaGouraya());
	assertNotNull(yemmaGourayaSource);
	assertTrue(isYemmaGourayaPicture(yemmaGourayaSource));
	
	InputStreamSource cornicheKabyleSource = imageServer.downloadImage(cornicheKabyle());
	assertNotNull(cornicheKabyleSource);
	assertTrue(isCornicheKabylePicture(cornicheKabyleSource));
	
	imageServer.deleteImage(yemmaGourayaId());
	imageServer.deleteImage(cornicheKabyleId());
    }
    
    @Test
    public void shouldUploadAndDownloadOriginalYemmaGourayaPictureUsingSeveralThreads() throws IOException, InterruptedException {
	uploadYemmaGourayaPicture();
	
	Iterable<Future<OperationStatus>> results = executorService.invokeAll(downloadYemmaGourayaJobs(TOTAL_NUMBER_OF_THREADS), TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
	assertTrue(allOperationsAreSuccessful(results));
	imageServer.deleteImage(yemmaGourayaId());
    }

    @Test
    public void shouldUploadAndDownloadDerivedYemmaGourayaPictureUsingSeveralThreads() throws IOException, InterruptedException {
	uploadYemmaGourayaPicture();
	
	Iterable<Future<OperationStatus>> results = executorService.invokeAll(downloadYemmaGouraya100x100Jobs(TOTAL_NUMBER_OF_THREADS), TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
	assertTrue(allOperationsAreSuccessful(results));
	imageServer.deleteImage(yemmaGourayaId());
    }
    
    @Test
    public void shouldCorrectlyReleaseConnectionsWhenExceptionsAreThrown() throws IOException {
	for(int i = 0 ; i < 100 ; i++) {
	    ImageReference imageReference = originalImage("anyImageThatNobodyHasEverUploadedOnThisPlanet");
	    try {
		InputStreamSource source = imageServer.downloadImage(imageReference);
		source.getInputStream();
		fail();
	    } catch(ResourceNotExistingException e) {
		assertEquals(imageReference, e.getImageReference());
	    }    
	}
    }
    
    private void uploadYemmaGourayaPicture() {
	imageServer.uploadImage(yemmaGourayaId(), JPEG, yemmaGourayaOriginalPictureStream());
    }
    
    private int numberOfUnsuccessfulOperations(Iterable<Future<OperationStatus>> results) {
	return Iterables.frequency(Iterables.transform(results, extractOperationStatusFunction()), OperationStatus.KO);
    }

    private int numberOfSuccessfulOperations(Iterable<Future<OperationStatus>> results) {
	return Iterables.frequency(Iterables.transform(results, extractOperationStatusFunction()), OperationStatus.OK);
    }
    
    private boolean allOperationsAreSuccessful(Iterable<Future<OperationStatus>> results) {
	return Iterables.all(Iterables.transform(results, extractOperationStatusFunction()), new Predicate<OperationStatus>(){
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
		} catch(Exception e){
		    throw new RuntimeException(e);
		}
	    }};
    }
    
    
    private List<Callable<OperationStatus>> downloadYemmaGouraya100x100Jobs(int numberOfThreads) {
	List<Callable<OperationStatus>> jobs = new ArrayList<Callable<OperationStatus>>();
	for(int i = 0 ; i < numberOfThreads ; i++) {
	    jobs.add(new DownloadPictureJob(imageServer, yemmaGouraya().rescaledTo(width(100).by(100)), yemmaGourayaDerived100x100PictureStream()));
	}
	return jobs;
    }
    
    private List<Callable<OperationStatus>> downloadYemmaGourayaJobs(int numberOfThreads) {
	List<Callable<OperationStatus>> jobs = new ArrayList<Callable<OperationStatus>>();
	for(int i = 0 ; i < numberOfThreads ; i++) {
	    jobs.add(new DownloadPictureJob(imageServer, yemmaGouraya(), yemmaGourayaOriginalPictureStream()));
	}
	return jobs;
    }
    
    private List<Callable<OperationStatus>> uploadYemmaGourayaJobs(int numberOfThreads) {
	List<Callable<OperationStatus>> jobs = new ArrayList<Callable<OperationStatus>>();
	for(int i = 0 ; i < numberOfThreads ; i++) {
	    jobs.add(new UploadPictureJob(imageServer, yemmaGourayaId(), yemmaGourayaOriginalPictureStream()));
	}
	return jobs;
    }
    
    private List<Callable<OperationStatus>> uploadCornicheKabyleJobs(int numberOfThreads) {
	List<Callable<OperationStatus>> jobs = new ArrayList<Callable<OperationStatus>>();
	for(int i = 0 ; i < numberOfThreads ; i++) {
	    jobs.add(new UploadPictureJob(imageServer, cornicheKabyleId(), PictureStreamSourceObjectMother.cornicheKabyleOriginalPictureStream()));
	}
	return jobs;
    }
   
}
