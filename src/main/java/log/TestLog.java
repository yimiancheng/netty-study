package log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TestLog
 *
 * @date 2019/8/20 14:53
 */
public class TestLog {
    private static final Logger LOG = LoggerFactory.getLogger(TestLog.class);

    public static void main(String[] args) {
        LOG.debug("DEBUG 日志");
        LOG.error("ERROR 日志");
    }
}
