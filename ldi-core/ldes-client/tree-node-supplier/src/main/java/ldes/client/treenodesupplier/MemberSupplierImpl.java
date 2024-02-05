package ldes.client.treenodesupplier;

import ldes.client.treenodesupplier.domain.valueobject.EndOfLdesException;
import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MemberSupplierImpl implements MemberSupplier {

    private static final long DEFAULT_WAITING_TIME_IN_SECONDS = 100;
    private final TreeNodeProcessor treeNodeProcessor;
    private final boolean keepState;
    private final Logger logger = LoggerFactory.getLogger(MemberSupplierImpl.class);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public MemberSupplierImpl(TreeNodeProcessor treeNodeProcessor, boolean keepState) {
        this.treeNodeProcessor = treeNodeProcessor;
        this.keepState = keepState;
        Runtime.getRuntime().addShutdownHook(new Thread(this::destroyState));
    }

    @Override
    public SuppliedMember get() {
        try {
            return executorService.submit(treeNodeProcessor::getMember).get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof EndOfLdesException endOfLdesException) {
                throw endOfLdesException;
            }
            throw new ClientInterruptedException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ClientInterruptedException(e);
        }
    }

    @Override
    public void destroyState() {
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(DEFAULT_WAITING_TIME_IN_SECONDS, TimeUnit.SECONDS)) {
                logger.warn("ExecutorService wasn't able to finish last task.");
                System.exit(0);
            }
            if (!keepState) {
                treeNodeProcessor.destroyState();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ClientInterruptedException(e);
        }
    }

}
