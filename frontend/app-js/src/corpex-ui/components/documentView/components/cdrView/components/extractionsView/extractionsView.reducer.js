import types from './extractionsView.types';
import docTypes from '../../../../documentView.types';
import colorStack from './extractionsData/colorStack';
import generateNewExtractions from './utilities/generateNewExtractions';

const initState = {
  tagMarkers: {},
  tagIndex: {},
  extrType: null,
  expandedTagTypes: [],
  checkedTagTypes: {}, // map tagType to tag marker color
  colorStack: [...colorStack],
};

function corpexExtractionsViewReducer(state = initState, action) {
  switch (action.type) {
    case docTypes.COMPLETE_GET_CDR: {
      return initState;
    }

    case docTypes.DOC_VIEW_CLEAR_STATE: {
      return initState;
    }

    case types.HIGHLIGHT_EXTRACTION_TAGS: {
      const newColorStack = [...state.colorStack];
      const newColor = newColorStack.length > 1 ? newColorStack.pop() : newColorStack[0];
      const newCheckedTagTypes = {
        ...state.checkedTagTypes,
        [action.tagType]: newColor,
      };

      return {
        ...state,
        checkedTagTypes: newCheckedTagTypes,
        colorStack: newColorStack,
      };
    }

    case types.REMOVE_EXTRACTION_TAGS_HIGHLIGHT: {
      const newCheckedTagTypes = { ...state.checkedTagTypes };
      const color = newCheckedTagTypes[action.tagType];
      const newColorStack = [...state.colorStack, color];
      Reflect.deleteProperty(newCheckedTagTypes, action.tagType);

      return {
        ...state,
        checkedTagTypes: newCheckedTagTypes,
        colorStack: newColorStack,
      };
    }

    case types.EXPAND_TAG_TYPE: {
      return {
        ...state,
        expandedTagTypes: [...state.expandedTagTypes, action.tagType],
      };
    }

    case types.UN_EXPAND_TAG_TYPE: {
      return {
        ...state,
        expandedTagTypes: state.expandedTagTypes.filter((tt) => tt !== action.tagType),
      };
    }

    case types.EXPAND_EXTRACTION_COMPONENT: {
      return {
        ...state,
        extrType: action.extrType,
        expandedTagTypes: [],
        checkedTagTypes: {},
        colorStack: [...colorStack],
      };
    }

    case types.UN_EXPAND_EXTRACTION_COMPONENT: {
      return {
        ...state,
        tagMarkers: {},
        tagIndex: {},
        extrType: null,
        expandedTagTypes: [],
        checkedTagTypes: {},
        colorStack: [...colorStack],
      };
    }

    case types.REGISTER_TAG_TYPE: {
      const newExtractions = generateNewExtractions(
        action.extrType,
        action.tagType,
        action.offsets,
        action.textArray,
        {
          tagIndex: state.tagIndex,
          tagMarkers: state.tagMarkers,
        },
      );

      return {
        ...state,
        tagIndex: newExtractions.tagIndex,
        tagMarkers: newExtractions.tagMarkers,
      };
    }

    case types.SET_TAG_STATE: {
      const {
        extrType,
        tagType,
        offset,
        stateUpdater,
      } = action;
      const index = `${extrType}_${tagType}_${offset}`;
      if (!(index in state.tagIndex)) return state;

      const oldTagState = state.tagIndex[index].state;
      const newTagState = stateUpdater(oldTagState);

      return {
        ...state,
        tagIndex: {
          ...state.tagIndex,
          [index]: {
            ...state.tagIndex[index],
            state: newTagState,
          },
        },
      };
    }

    default: {
      return state;
    }
  }
}

export default corpexExtractionsViewReducer;
