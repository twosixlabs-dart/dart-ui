import React from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import { Paper } from '@material-ui/core';
import { componentTypes } from '../../../componentData/corpusViewUserComponentTypes';
import DateComponentPopup from './DateComponentPopup';
import KeywordComponentPopup from './KeywordComponentPopup';
import NumberComponentPopup from './NumberComponentPopup';
import FacetComponentPopup from './FacetComponentPopup';
import TagComponentPopup from './TagComponentPopup';
import SectionComponentPopup from './SectionComponentPopup';

import { connect } from '../../../../../../dart-ui/context/CustomConnect';

const styles = (theme) => ({
  paper: {
    position: 'absolute',
    width: 400,
    backgroundColor: theme.palette.background.paper,
    border: '2px solid #000',
    boxShadow: theme.shadows[5],
    padding: theme.spacing(2, 4, 3),
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
  },
});

function CorpusViewerComponentPopupMenu({
  componentType,
  closePopup,
  sectionId,
  addComponent,
  dispatch,
  classes,
}) {
  let component;

  const childProps = {
    sectionId,
    addComponent,
    closePopup,
    dispatch,
  };

  switch (componentType) {
    case componentTypes.DATE_TIME_COMPONENT: {
      component = (
        <DateComponentPopup
          {...childProps}
        />
      );
      break;
    }

    case componentTypes.FACTIVA_FACETS_COMPONENT: {
      component = (
        <FacetComponentPopup
          {...childProps}
          facetIds={['factiva-industry', 'factiva-subject', 'factiva-region']}
          kind="no-score"
          labelLabel="Value"
        />
      );
      break;
    }

    case componentTypes.EVENTS_COMPONENT: {
      component = (
        <TagComponentPopup
          {...childProps}
          tagId="qntfy-event"
          defaultTagType="Conflict"
          labelLabel="Event"
        />
      );
      break;
    }

    case componentTypes.NER_COMPONENT: {
      component = (
        <TagComponentPopup
          {...childProps}
          tagId="qntfy-ner"
          defaultTagType="GPE"
          labelLabel="Entity"
        />
      );
      break;
    }

    case componentTypes.TEXT_LENGTH_COMPONENT: {
      component = (
        <NumberComponentPopup
          {...childProps}
        />
      );
      break;
    }

    case componentTypes.KEYWORD_METADATA_COMPONENT: {
      component = (
        <KeywordComponentPopup
          {...childProps}
        />
      );
      break;
    }

    case componentTypes.TOPIC_COMPONENT: {
      component = (
        <FacetComponentPopup
          {...childProps}
          facetIds={['qntfy-topic']}
          kind="with-score"
          labelLabel="Topic"
        />
      );
      break;
    }

    case componentTypes.SENTIMENT_SUBJECTIVITY_COMPONENT: {
      component = (
        <FacetComponentPopup
          {...childProps}
          facetIds={['qntfy-sentiment']}
          kind="with-score"
          labelLabel="Analytic"
          valueLabel="Measurement"
        />
      );
      break;
    }

    case componentTypes.SECTION_COMPONENT: {
      component = (
        <SectionComponentPopup
          {...childProps}
        />
      );
      break;
    }

    default:
      component = <div />;
  }

  return (
    <div>
      <Paper classes={{ root: classes.paper }}>
        {component}
      </Paper>
    </div>
  );
}

CorpusViewerComponentPopupMenu.propTypes = {
  sectionId: PropTypes.oneOf([undefined, PropTypes.string]).isRequired,
  addComponent: PropTypes.func.isRequired,
  componentType: PropTypes.string.isRequired,
  closePopup: PropTypes.func.isRequired,
  dispatch: PropTypes.func.isRequired,
  classes: PropTypes.arrayOf(PropTypes.string).isRequired,
};

function mapStateToProps() {
  return {
  };
}

export default connect(mapStateToProps)(withStyles(styles)(CorpusViewerComponentPopupMenu));
