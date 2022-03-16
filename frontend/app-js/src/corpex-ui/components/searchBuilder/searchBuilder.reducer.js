import types from './searchBuilder.types';
import generateComponentProperties from './utilities/generateComponentProperties';
import corpexBootstrap from './utilities/corpexBootstrap';

const initState = corpexBootstrap;

function corpexSearchBuilderReducer(state = initState, action) {
  switch (action.type) {
    case types.ADD_COMPONENT: {
      const newIndex = { ...state.componentIndex };

      newIndex[action.id] = generateComponentProperties(action.componentType);

      return {
        ...state,
        componentIndex: newIndex,
      };
    }

    case types.UPDATE_ROOT_COMPONENT_MAP: {
      return {
        ...state,
        rootComponentMap: action.componentMap,
      };
    }

    case types.REMOVE_COMPONENT: {
      const newIndex = { ...state.componentIndex };
      Reflect.deleteProperty(newIndex, action.id);

      return {
        ...state,
        componentIndex: newIndex,
      };
    }

    case types.UPDATE_COMPONENT_BOOL_TYPE: {
      const newIndex = { ...state.componentIndex };
      newIndex[action.id].boolType = action.boolType;

      return {
        ...state,
        componentIndex: newIndex,
      };
    }

    case types.TOGGLE_COMPONENT_EDITED: {
      const newIndex = { ...state.componentIndex };
      newIndex[action.id].isEdited = !state.componentIndex[action.id].isEdited;

      return {
        ...state,
        componentIndex: newIndex,
      };
    }

    case types.UPDATE_COMPONENT_STATE: {
      const newIndex = { ...state.componentIndex };
      if (action.newState !== null && action.newState !== undefined) {
        newIndex[action.id].componentState = action.newState;
      }
      if (action.isActive !== null && action.isActive !== undefined) {
        newIndex[action.id].isActive = action.isActive;
      }
      if (action.query !== null && action.query !== undefined) {
        newIndex[action.id].query = action.query;
      }
      if (action.commonAggQueries !== null && action.commonAggQueries !== undefined) {
        newIndex[action.id].commonAggQueries = action.commonAggQueries;
      }
      if (action.privateAggQueries !== null && action.privateAggQueries !== undefined) {
        newIndex[action.id].privateAggQueries = action.privateAggQueries;
      }
      if (action.summary !== null && action.summary !== undefined) {
        newIndex[action.id].summary = action.summary;
      }

      return {
        ...state,
        componentIndex: newIndex,
      };
    }

    case types.COMPLETE_FACET_QUERY: {
      const newComponentIndex = { ...state.componentIndex };
      newComponentIndex[action.componentId] = {
        ...state.componentIndex[action.componentId],
        componentState: {
          ...state.componentIndex[action.componentId].componentState,
          hasScore: action.facetObj.has_score,
        },
      };

      return {
        ...state,
        componentIndex: newComponentIndex,
      };
    }

    case types.COMPLETE_TAG_TYPE_QUERY: {
      const newComponentIndex = { ...state.componentIndex };
      newComponentIndex[action.componentId] = {
        ...state.componentIndex[action.componentId],
        componentState: {
          ...state.componentIndex[action.componentId].componentState,
          tagTypes: action.tagTypes,
        },
      };

      return {
        ...state,
        componentIndex: newComponentIndex,
      };
    }

    default: {
      return state;
    }
  }
}

export default corpexSearchBuilderReducer;
