import types from './corpusView.types';
import uuidv4 from '../../../common/utilities/helpers';
import componentTypes from './componentData/corpusViewComponentTypes';

const initState = {
  aggQueries: {},
  componentMap: [], // list of components at root level
  componentIndex: {},
};

/**
 *  componentIndex should have the following form:
 *  {
 *    component_uuid_1: {
 *      type: "xxxxx",
 *      aggs: {},
 *      label: "Component Label",
 *      layout: {},     // information about size/position
 *      state: {},      // where each component keeps state
 *    },
 *    comonent_uuid_2: {
 *      type: "SECTION",
 *      aggs: {},       // should always be empty for 'section' component
 *      label: "Section Title",
 *      layout: {},
 *      state: {
 *        componentMap: [], // list of more component uuids
 *      },
 *    }
 *  }
 */

function corpexCorpusViewReducer(state = initState, action) {
  switch (action.type) {
    case types.CORPUS_VIEW_UPDATE_ALL_AGGS: {
      return {
        ...state,
        aggQueries: {
          ...state.aggQueries,
          ...action.aggQueries,
        },
      };
    }

    case types.CORPUS_VIEW_UPDATE_COMPONENT: {
      const newComponentIndex = {
        ...state.componentIndex,
      };

      newComponentIndex[action.componentId] = {
        ...state.componentIndex[action.componentId],
        ...action.data,
      };

      return {
        ...state,
        componentIndex: newComponentIndex,
      };
    }

    case types.CORPUS_VIEW_UPDATE_COMPONENT_STATE: {
      const newComponentIndex = {
        ...state.componentIndex,
      };

      newComponentIndex[action.componentId] = {
        ...newComponentIndex[action.componentId],
        state: {
          ...state.componentIndex[action.componentId].state,
          ...action.state,
        },
      };

      return {
        ...state,
        componentIndex: newComponentIndex,
      };
    }

    case types.CORPUS_VIEW_ADD_COMPONENT: {
      const newId = action.componentId === undefined ? uuidv4() : action.componentId;

      const newComponentIndex = {
        ...state.componentIndex,
        [newId]: action.data,
      };

      const newComponentMap = [...state.componentMap];

      if (action.sectionId === undefined) {
        newComponentMap.push(newId);
      } else {
        const newSectionMap = [...state.componentIndex[action.sectionId].state.componentMap];
        newSectionMap.push(newId);
        newComponentIndex[action.sectionId] = {
          ...state.componentIndex[action.sectionId],
          state: {
            ...state.componentIndex[action.sectionId].state,
            componentMap: newSectionMap,
          },
        };
      }

      const newAggQueries = {};
      if (action.sectionId === undefined) {
        Object.keys(action.data.aggs).forEach((aggId) => {
          const updatedId = `${newId}-${aggId}`;
          newAggQueries[updatedId] = action.data.aggs[aggId];
        });
      } else {
        Object.keys(action.data.aggs).forEach((aggId) => {
          const updatedId = `${action.sectionId}-${aggId}`;
          newAggQueries[updatedId] = action.data.aggs[aggId];
        });
        newComponentIndex[action.sectionId] = {
          ...newComponentIndex[action.sectionId],
          aggs: {
            ...newComponentIndex[action.sectionId].aggs,
            ...newAggQueries,
          },
        };
      }

      return {
        ...state,
        componentMap: newComponentMap,
        componentIndex: newComponentIndex,
        aggQueries: {
          ...state.aggQueries,
          ...newAggQueries,
        },
      };
    }

    case types.CORPUS_VIEW_REMOVE_COMPONENT: {
      let removalIds = [action.componentId];
      if (state.componentIndex[action.componentId].type === componentTypes.SECTION_COMPONENT) {
        removalIds = removalIds.concat(state.componentIndex[action.componentId].state.componentMap);
      }

      const newComponentIndex = {};
      Object.keys(state.componentIndex).forEach((cId) => {
        if (!removalIds.includes(cId)) newComponentIndex[cId] = state.componentIndex[cId];
      });

      const newComponentMap = state.componentMap.filter((cId) => !removalIds.includes(cId));

      const newAggQueries = {};
      Object.keys(state.aggQueries).forEach((aggId) => {
        if (removalIds.every((cId) => !aggId.includes(cId))) {
          newAggQueries[aggId] = state.aggQueries[aggId];
        }
      });

      return {
        ...state,
        componentMap: newComponentMap,
        componentIndex: newComponentIndex,
        aggQueries: {
          ...newAggQueries,
        },
      };
    }

    case types.CORPUS_VIEW_UPDATE_COMPONENT_MAP: {
      return {
        ...state,
        componentMap: action.componentMap,
      };
    }

    default: {
      return state;
    }
  }
}

export default corpexCorpusViewReducer;
