package be.vlaanderen.informatievlaanderen.ldes.poller.exceptions;

public class SchedulerInterruptedException extends RuntimeException{

    public SchedulerInterruptedException(Throwable cause) {
        super(cause);
    }
}
