//package com.grendelscan.tests.testJobs;
//
//import com.grendelscan.requester.authentication.AuthenticationPackage;
//import com.grendelscan.scan.InterruptedScanException;
//import com.grendelscan.tests.testTypes.ByAuthenticationPackageTest;
//
//public class ByAuthenticationPackageTestJob extends TestJob
//{
//	/**
//	 * 
//	 */
//	private static final long	serialVersionUID	= 1L;
//	private AuthenticationPackage	authenticationPackage;
//
//	public ByAuthenticationPackageTestJob(int moduleNumber, AuthenticationPackage authenticationPackage)
//	{
//		super(moduleNumber);
//		this.authenticationPackage = authenticationPackage;
//	}
//
//	@Override
//	public void internalRunTest() throws InterruptedScanException
//	{
//		((ByAuthenticationPackageTest) getModule()).testAuthenticationPackage(authenticationPackage);
//	}
//}
