import { extractionTypes } from '../components/documentView/components/cdrView/components/extractionsView/extractionsData/enums';

const bootstrapExtractions = (extrType) => {
  switch (extrType) {
    case extractionTypes.NER:
      return {
        title: 'Qntfy Named Entity Recognition',
      };

    case extractionTypes.EVENT:
      return {
        title: 'Qntfy Event Detection',
      };

    default: {
      return {};
    }
  }
};

export default bootstrapExtractions;
