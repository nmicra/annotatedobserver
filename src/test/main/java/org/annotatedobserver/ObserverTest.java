package org.annotatedobserver;

import org.annotatedobserver.dao.CaseDaoI;
import org.annotatedobserver.dao.TempDao;
import org.annotatedobserver.data.Case;
import org.annotatedobserver.listener.TestEventObserver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ObserverSpringConfiguration.class)
public class ObserverTest {

	@Autowired
	private CaseDaoI caseDao;

	@Autowired
	private TempDao tempDao;

	@Autowired
	private TestEventObserver testEventObserver;

	@Test
	public void generalTest() {
		testEventObserver.cleanup();

		caseDao.printCase(new Case(1, "bla"));
		caseDao.saveCase(new Case(2, "kuku"));
		caseDao.printCase(new Case(5, "bla4"));
		tempDao.deleteCase(new Case(3, "eeee"));

		Assert.assertEquals(2, testEventObserver.getSaveCase());
		Assert.assertEquals(7, testEventObserver.getPrintCase());
		Assert.assertEquals(1, testEventObserver.getDeleteCase());
		Assert.assertEquals(0, testEventObserver.getNegativeExample());

	}


	@Test
	public void withEventName() {
		testEventObserver.cleanup();
		tempDao.tempCase(new Case(3, "eeee"));
		Assert.assertEquals(0, testEventObserver.getTempCase()); // tempCaseExampleObserver - shouldn't be invoked
		Assert.assertEquals(1, testEventObserver.getTempCase2()); // tempCase2ExampleObserver - have to be invoked
	}

	@Test
	public void registeredToMultipleEvents() {
		testEventObserver.cleanup();
		tempDao.tempCase(new Case(3, "eeee"));
		tempDao.deleteCase(new Case(3, "eeee"));
		caseDao.printCase(new Case(2, "kuku"));
		tempDao.deleteCase(new Case(3, "eeee"));
		Assert.assertEquals(4, testEventObserver.getMultiple()); // tempCaseExampleObserver - shouldn't be invoked
	}

	@Test
	public void runAsync() throws InterruptedException {
		testEventObserver.cleanup();
		for (int i = 0; i < 20; i++) {
			caseDao.saveCase(new Case(i, "kuku"));
		}
		Thread.currentThread().sleep(300);
		Assert.assertEquals(testEventObserver.getSaveCase(), testEventObserver.getAsyc());
	}

}








