import settings from '../../../config/settings';
import uuidv4 from '../../../../common/utilities/helpers';
import generateComponentProperties from './generateComponentProperties';

const rootComponentMap = [];
const componentIndex = {};

settings.DEFAULT_COMPONENTS.forEach((componentType) => {
  const id = uuidv4();
  rootComponentMap.push(id);
  componentIndex[id] = generateComponentProperties(componentType);
});

const corpexBootstrap = {
  rootComponentMap,
  componentIndex,
};

export default corpexBootstrap;
