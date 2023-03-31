package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

public class SchedulerInterruptedException extends RuntimeException{

    public SchedulerInterruptedException(Throwable cause) {
        super(cause);
    }
}
