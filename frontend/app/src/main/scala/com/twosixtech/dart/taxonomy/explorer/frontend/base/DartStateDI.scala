package com.twosixtech.dart.taxonomy.explorer.frontend.base

import com.twosixtech.dart.taxonomy.explorer.frontend.app.error.ErrorHandlerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.explorer.DartConceptExplorerDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.frame.DartFrameDI
import com.twosixtech.dart.taxonomy.explorer.frontend.app.common.loading.DartLoadingDI
import com.twosixtech.dart.taxonomy.explorer.models.DartConceptDeps

trait DartStateDI {
    this : DartConceptDeps
      with DartFrameDI
      with DartConceptExplorerDI
      with ErrorHandlerDI
      with DartLoadingDI =>

    case class DartState(
        coreState : CoreState,
        layoutState : LayoutState,
    )

    case class CoreState(
        frameState : DartFrame.State = DartFrame.State(),
        conceptState : DartConceptExplorer.State = DartConceptExplorer.State(),
        errorState : ErrorHandler.State = ErrorHandler.State(),
        loadingState : DartLoading.State = DartLoading.State(),
    )

    case class LayoutState( )

}
