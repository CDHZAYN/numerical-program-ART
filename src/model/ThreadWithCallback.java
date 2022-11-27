package model;

import faultZone.FaultZone;

import java.util.concurrent.Callable;

public  class ThreadWithCallback implements Callable {
		private DomainBoundary inpuBoundary;
		private FaultZone fz;
		private AbstractART method;

		public ThreadWithCallback(DomainBoundary inputBoundary, AbstractART method, FaultZone fz) {
			this.inpuBoundary = inputBoundary;
			this.fz=fz;
			this.method=method;
		}

		@Override
		public Object call() throws Exception {
			int temp = this.method.run(fz);
			System.out.println("sdfdfs");
			return temp;
		}

	}