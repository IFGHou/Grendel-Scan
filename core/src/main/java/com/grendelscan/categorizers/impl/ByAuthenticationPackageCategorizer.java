package com.grendelscan.categorizers.impl;
///*
// * AllTransactionsCategorizer.java
// * 
// * Created on September 15, 2007, 8:59 PM
// * 
// * To change this template, choose Tools | Template Manager
// * and open the template in the editor.
// */
//
//package com.grendelscan.categorizers.impl;
//
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//import com.grendelscan.categorizers.SingleSetCategorizer;
//import com.grendelscan.requester.authentication.AuthenticationPackage;
//import com.grendelscan.scan.Scan;
//import com.grendelscan.tests.testJobs.ByAuthenticationPackageTestJob;
//import com.grendelscan.tests.testJobs.TestJob;
//import com.grendelscan.smashers.AbstractSmasher;
//import com.grendelscan.smashers.types.ByAuthenticationPackageTest;
//
///**
// * 
// * @author Administrator
// */
//
//
//public class ByAuthenticationPackageCategorizer extends SingleSetCategorizer
//{
//
//	/** Creates a new instance of AllTransactionsCategorizer */
//	public ByAuthenticationPackageCategorizer()
//	{
//		super(ByAuthenticationPackageTest.class);
//	}
//
//
//	public void analyzeAuthenticationPackage(AuthenticationPackage authenticationPackage)
//	{
//		Map<AbstractSmasher, Set<TestJob>> tests = new HashMap<AbstractSmasher, Set<TestJob>>();
//		synchronized (testModules)
//		{
//			for (AbstractSmasher testModule: testModules)
//			{
//				ByAuthenticationPackageTestJob testJob = 
//					new ByAuthenticationPackageTestJob(testModule.getModuleNumber(), authenticationPackage);
//				addJobToCollection(testJob, testModule, tests);
//			}
//		}
//		Scan.getInstance().getTesterQueue().submitJobs(tests);
//	}
//}
