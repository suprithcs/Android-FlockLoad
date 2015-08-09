package com.flockload.flockload;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface AsyncResponse {
//	/void processFinish(String output);

	void processFinish(Integer result) throws InterruptedException, ExecutionException;

	void processFinish(DownloadParams dp)throws InterruptedException, ExecutionException, IOException;

}
