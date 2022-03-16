import throttle from 'lodash/throttle';
import { STORAGE_SUBSCRIPTION_THROTTLE_INTERVAL } from '../config/constants';
import getConfig from './getConfig';
import version from './getVersion';

// DART_UI_ENV will be overwritten by webpack
// eslint-disable-next-line no-undef
const storageKey = `dartUiState-${version}-${process.env.DART_UI_ENV || 'no-env'}-${process.env.NODE_ENV}`;

const loadState = () => {
  try {
    const serializedState = localStorage.getItem(storageKey);
    if (serializedState === null) {
      console.log('No state saved!');
      return undefined;
    }
    const parsedState = JSON.parse(serializedState);
    console.log('Saved state:');
    console.log(parsedState);
    return parsedState;
  } catch (err) {
    console.log('failed to retrieve state');
    return undefined;
  }
};

// const saveState = (state) => {
//   try {
//     const serializedState = JSON.stringify(state);
//     localStorage.setItem(storageKey, serializedState);
//   } catch {
//     console.log('failed to write state');
//   }
// };

const keyMap = [];

const addToKeyMap = (component) => {
  const componentConfig = getConfig(component);
  if (componentConfig && componentConfig.SUBSCRIPTIONS) {
    keyMap.push([component, ...componentConfig.SUBSCRIPTIONS]);
  }
};

// addToKeyMap('common');
// addToKeyMap('dart');
// addToKeyMap('forklift');
addToKeyMap('corpex');
// addToKeyMap('docStatus');

// The following functions rely on keyMap

// updates `state` with a section of state called `update,` only changing
// values of keys stored in `keyMap`
const updateState = (state, update) => {
  const newState = { ...state };
  if (!Array.isArray(keyMap) || keyMap.length === 0) return newState;

  const updateStateInternal = (stateInternal, updateInternal, keyArray, index) => {
    const newStateInternal = {
      ...stateInternal,
    };

    if (!Array.isArray(keyArray)) {
      return updateInternal;
    }

    if (Array.isArray(keyArray[index])) {
      keyArray[index].forEach((nextKey) => {
        let key = nextKey;
        if (Array.isArray(nextKey)) {
          [key] = nextKey;
        }
        newStateInternal[key] = updateStateInternal(
          stateInternal[nextKey],
          updateInternal[nextKey],
          nextKey,
          0,
        );
      });
    } else if (index === keyArray.length - 1) {
      newStateInternal[keyArray[index]] = updateStateInternal(
        stateInternal[keyArray[index]],
        updateInternal[keyArray[index]],
        keyArray[index],
      );
    } else {
      newStateInternal[keyArray[index]] = updateStateInternal(
        stateInternal[keyArray[index]],
        updateInternal[keyArray[index]],
        keyArray,
        index + 1,
      );
    }

    return newStateInternal;
  };

  keyMap.forEach((keyArray) => {
    console.log(keyArray);
    newState[keyArray[0]] = updateStateInternal(state, update, keyArray, 0)[keyArray[0]];
  });

  return newState;
};

// gets a section of `state`, only of keys stored in `keyMap`
const getStateSection = (state) => {
  if (!Array.isArray(keyMap) || keyMap.length === 0) return null;
  const stateSection = {};

  const getStateSectionInternal = (stateInternal, keyArray, index) => {
    const newStateInternal = {};

    if (!Array.isArray(keyArray)) {
      return stateInternal;
    }

    if (Array.isArray(keyArray[index])) {
      keyArray[index].forEach((nextKey) => {
        let key = nextKey;
        if (Array.isArray(nextKey)) {
          [key] = nextKey;
        }
        newStateInternal[key] = getStateSectionInternal(
          stateInternal[nextKey],
          nextKey,
          0,
        );
      });
    } else if (index === keyArray.length - 1) {
      newStateInternal[keyArray[index]] = getStateSectionInternal(
        stateInternal[keyArray[index]],
        keyArray[index],
      );
    } else {
      newStateInternal[keyArray[index]] = getStateSectionInternal(
        stateInternal[keyArray[index]],
        keyArray,
        index + 1,
      );
    }

    return newStateInternal;
  };

  keyMap.forEach((keyArray) => {
    stateSection[keyArray[0]] = getStateSectionInternal(state, keyArray, 0)[keyArray[0]];
  });

  return stateSection;
};

const persistState = (state, stateSaver) => {
  // saveState(getStateSection(state));
  stateSaver(getStateSection(state));
};

export const updateStateFromSection = (state, stateSection) => {
  try {
    if (!stateSection || Object.keys(stateSection).length === 0) return state;
    return updateState(state, stateSection);
  } catch (err) {
    console.log('Unable to update state');
    return state;
  }
};

export const updateStateFromLocalStorage = (state) => {
  try {
    const stateSection = loadState();
    if (!stateSection || Object.keys(stateSection).length === 0) return state;
    return updateState(state, stateSection);
  } catch (err) {
    console.log('Unable to update state');
    return undefined;
  }
};

export const subscription = (store, stateSaver) => throttle(() => {
  const state = store.getState();
  persistState(state, stateSaver);
}, STORAGE_SUBSCRIPTION_THROTTLE_INTERVAL);
