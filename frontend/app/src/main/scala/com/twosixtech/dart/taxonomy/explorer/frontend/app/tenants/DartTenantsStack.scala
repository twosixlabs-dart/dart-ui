package com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants

import com.twosixtech.dart.taxonomy.explorer.frontend.DartApp
import com.twosixtech.dart.taxonomy.explorer.frontend.app.tenants.layouts.GenericDartTenantsLayoutDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.DartComponentDI
import com.twosixtech.dart.taxonomy.explorer.frontend.base.context.{ DartContextDeps, GenericDartContextDI }
import com.twosixtech.dart.taxonomy.explorer.frontend.configuration.{ DartConfigDeps, GenericDartConfigDI }

object DartTenantsStack {

	trait Base extends DartTenantsDI {
		this : DartComponentDI
		  with DartTenantsLayoutDeps
		  with DartContextDeps
		  with DartConfigDeps =>
	}

	trait Generic
	  extends Base
		with GenericDartTenantsLayoutDI {
		this : DartApp.WmDependencies
		  with DartApp.TestableDependencies =>
	}


}
