import { combineReducers } from 'redux';

import types from './cdrView.types';
import docViewTypes from '../../documentView.types';
import corpexExtractionsViewReducer from './components/extractionsView/extractionsView.reducer';

const initState = {
  text: [],
  tagFocus: {
    tagType: null,
    offset: null,
  },
  tagHover: {
    tagType: null,
    offset: null,
  },
  scrollTo: {
    window: null, // which window to scroll: 'text' or 'extr'
    tagType: null, // to identify tag to scroll to
    offset: null, // to identify tag to scroll to (starting offset)
    scrolledToIndex: false, // Set once windowed list has scrolled to tag
    scrollToView: null, // Set by tag itself for final scroll (set to scroll change)
  },
};

function corpexCdrViewWrapperReducer(state = initState, action) {
  switch (action.type) {
    case types.SET_CDR_TEXT_ARRAY: {
      return {
        ...state,
        text: action.textArray,
      };
    }

    case docViewTypes.COMPLETE_GET_CDR: {
      return initState;
    }

    case docViewTypes.DOC_VIEW_CLEAR_STATE: {
      return initState;
    }

    case types.CLEAR_CDR_VIEW: {
      return initState;
    }

    case types.FOCUS_TAG: {
      const newScrollTo = { ...initState.scrollTo };
      if (action.tagType in action.checkedTagTypes) {
        if (action.window === 'text') {
          newScrollTo.window = 'extr';
        } else if (action.window === 'extr') {
          newScrollTo.window = 'text';
        }
        newScrollTo.offset = action.offset;
        newScrollTo.tagType = action.tagType;
      }

      return {
        ...state,
        tagFocus: {
          tagType: action.tagType,
          offset: action.offset,
        },
        scrollTo: newScrollTo,
      };
    }

    case types.UNFOCUS_TAG: {
      const { tagType, offset } = action;
      const stateTagType = state.tagFocus.tagType;
      const stateOffset = state.tagFocus.offset;
      const isSame = tagType === stateTagType && offset === stateOffset;
      const newTagFocus = isSame ? initState.tagFocus : state.tagFocus;
      const newTagHover = isSame ? initState.tagHover : state.tagHover;
      return {
        ...state,
        tagFocus: newTagFocus,
        tagHover: newTagHover,
      };
    }

    case types.HOVER_TAG: {
      return {
        ...state,
        tagHover: {
          tagType: action.tagType,
          offset: action.offset,
        },
      };
    }

    case types.UNHOVER_TAG: {
      return {
        ...state,
        tagHover: initState.tagHover,
      };
    }

    case types.COMPLETE_SCROLL_TO_INDEX: {
      return {
        ...state,
        scrollTo: {
          ...state.scrollTo,
          scrolledToIndex: true,
        },
      };
    }

    case types.SET_SCROLL_TO_VIEW: {
      return {
        ...state,
        scrollTo: {
          ...state.scrollTo,
          scrollToView: action.offset,
          scrolledToIndex: false,
        },
      };
    }

    case types.COMPLETE_SCROLL_TO: {
      return {
        ...state,
        scrollTo: {
          ...state.scrollTo,
          window: initState.scrollTo.window,
          offset: initState.scrollTo.offset,
          tagType: initState.scrollTo.tagType,
          scrollToView: initState.scrollTo.scrollToView,
          scrolledToIndex: initState.scrollTo.scrolledToIndex,
        },
      };
    }

    default: {
      return state;
    }
  }
}

export default combineReducers({
  root: corpexCdrViewWrapperReducer,
  extractions: corpexExtractionsViewReducer,
});
