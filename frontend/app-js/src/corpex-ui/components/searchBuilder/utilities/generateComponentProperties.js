import bootStrapComponent from '../../../config/componentBootstrap';

const generateComponentProperties = (componentType) => ({
  componentType,
  boolType: bootStrapComponent(componentType).boolType,
  title: bootStrapComponent(componentType).title,
  query: {},
  privateAggQueries: {},
  commonAggQueries: {},
  summary: '',
  isEdited: true,
  isActive: false,
  componentState: bootStrapComponent(componentType).componentState,
});

export default generateComponentProperties;
