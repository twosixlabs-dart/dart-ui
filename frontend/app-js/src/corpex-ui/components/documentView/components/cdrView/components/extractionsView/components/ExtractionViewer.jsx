import React, { Component } from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import Grid from '@material-ui/core/Grid';

import DartAccordion from '../../../../../../../../common/components/DartAccordion';

import settings from '../../../../../../../config/settings';
import {
  extractionTypes,
} from '../extractionsData/enums';
import bootstrapExtractions from '../../../../../../../config/extractionsBootstrap';
import OffsetTagExtractionComponent from './OffsetTagExtractionComponent';
import { expandExtractionComponent, unExpandExtractionComponent } from '../extractionsView.actions';

import { connect } from '../../../../../../../../dart-ui/context/CustomConnect';

const styles = () => ({
});

class ExtractionViewer extends Component {
  render() {
    const {
      activeExtraction,
      windowDimensions,
      cdr,
      dispatch,
    } = this.props;

    if (!cdr || !cdr.document_id) return <div />;
    const docId = cdr.document_id;

    const childProps = {
      ...this.props,
      docId,
      windowDimensions,
    };

    const accordionHandler = (extrType) => (e, isExpanded) => {
      if (isExpanded) dispatch(expandExtractionComponent(extrType));
      else dispatch(unExpandExtractionComponent(extrType));
    };

    const getExtrEle = (extrName) => {
      switch (extrName) {
        case extractionTypes.NER:
          return (
            <OffsetTagExtractionComponent
              extrType={extractionTypes.NER}
              annotationLabel="qntfy-ner-annotator"
              {...childProps}
            />
          );

        case extractionTypes.EVENT:
          return (
            <OffsetTagExtractionComponent
              extrType={extractionTypes.EVENT}
              annotationLabel="qntfy-events-annotator"
              {...childProps}
            />
          );

        default:
          return '';
      }
    };

    const components = settings.EXTRACTIONS.map((extrType) => {
      const extrEle = getExtrEle(extrType);
      const bootstrap = bootstrapExtractions(extrType);
      return (
        <DartAccordion
          key={`extraction-component-${extrType.toLowerCase()}`}
          id={extrType}
          title={bootstrap.title}
          expanded={activeExtraction === extrType}
          onChange={accordionHandler(extrType)}
          className={`extraction-component-${extrType}`}
        >
          {extrEle}
        </DartAccordion>
      );
    });

    return (
      <Grid container direction="row" ref={this.windowRef} className="extraction-viewer">
        <Grid item xs={12}>
          {components}
        </Grid>
      </Grid>
    );
  }
}

ExtractionViewer.propTypes = {
  // cdrTextWindowRef: PropTypes.element.isRequired,
  // cdrExtrWindowRef: PropTypes.element.isRequired,
  addTagRefExtr: PropTypes.func.isRequired,
  // tagRefs: PropTypes.shape({}).isRequired,
  activeExtraction: PropTypes.string,
  windowDimensions: PropTypes.shape({}).isRequired,
  cdr: PropTypes.shape({
    document_id: PropTypes.string,
  }).isRequired,
  dispatch: PropTypes.func.isRequired,
};

ExtractionViewer.defaultProps = {
  activeExtraction: null,
};

function mapStateToProps(state) {
  return {
    activeExtraction: state.corpex.documentView.cdrView.extractions.extrType,
    cdr: state.corpex.documentView.root.cdr,
  };
}

export default connect(mapStateToProps)(withStyles(styles)(ExtractionViewer));
