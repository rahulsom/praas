import grails.test.AbstractCliTestCase

class DataloadTests extends AbstractCliTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testDataload() {

        execute(["dataload"])

        assertEquals 0, waitForProcess()
        verifyHeader()
    }
}
