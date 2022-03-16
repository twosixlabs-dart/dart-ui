import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { toPairs } from 'lodash';

import withStyles from '@material-ui/core/styles/withStyles';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import Accordion from '@material-ui/core/Accordion';
import AccordionSummary from '@material-ui/core/AccordionSummary';
import AccordionDetails from '@material-ui/core/AccordionDetails';

import StickyHeader from '../../../../common/components/layout/StickyHeader';
import Header from '../../../../common/components/Header';
import {
  author, capSrc, classification,
  contType,
  creator,
  docIdLink,
  docType,
  language,
  pages,
  producer,
  pubDate,
  publisher,
  subject,
  team,
  timestamp,
  title,
  labels,
  facets,
  genre,
} from '../../../utilities/cdrReader';
import DartAccordion from '../../../../common/components/DartAccordion';

import { connect } from '../../../../dart-ui/context/CustomConnect';

const styles = (theme) => ({
  bodyPaper: {
    padding: 15,
    paddingTop: 15,
    marginTop: 1,
    marginBottom: 1,
    '&:first-child': {
      borderTopLeftRadius: 4,
      borderTopRightRadius: 4,
    },
  },
  accordionClosed: {
    marginTop: theme.spacing(0),
  },
  accordionExpanded: {
    marginTop: theme.spacing(3),
    marginBottom: theme.spacing(2),
  },
});

class MetadataPanel extends Component {
  render() {
    const {
      tenantId,
      cdr,
      aggregations,
      wordCount,
      docId,
      xhrHandler,
      logger,
    } = this.props;

    if (cdr === null) return <div />;
    if (cdr.document_id !== docId) return <div />;

    const header = <Header small title="Document Metadata" />;

    const mLine = (label, value, key) => (value === '' ? '' : (
      <Grid item xs={12} className="doc-view-metadata-line" key={key}>
        <Typography component="div" variant="body1">
          <span><b>{`${label}: `}</b></span>
          <span>{value}</span>
        </Typography>
      </Grid>
    ));

    const aggsElements = aggregations === '' ? '' : toPairs(aggregations)
      .map(([label, values]) => (
        mLine(label, values, `agg-element-${label}`)
      ));

    const facetAnnotations = cdr.annotations.filter((annotation) => annotation.type === 'facets');
    const facetsObj = {};
    facetAnnotations.forEach((annotation) => { facetsObj[annotation.label] = annotation.content; });
    const allFacets = facets({ facets: facetsObj });
    const facetsElements = allFacets === '' ? '' : toPairs(allFacets)
      .map(([label, values]) => (
        mLine(label, values, `metadata-facet-${label}`)
      ));

    const labelsTxt = labels(cdr);
    const labelsElement = labelsTxt === '' ? '' : (
      <Accordion expanded={false} className="doc-view-metadata-labels">
        <AccordionSummary
          expandIcon=""
          aria-controls="panel-labels-content"
          id="panel-labels-header"
        >
          <Typography component="div" variant="body1">
            <Grid container spacing={2} alignItems="center">
              <Grid item><b>Labels:</b></Grid>
              <Grid>{labelsTxt}</Grid>
            </Grid>
          </Typography>
        </AccordionSummary>
        <AccordionDetails />
      </Accordion>
    );

    const body = (
      <Grid
        container
        direction="column"
        alignItems="stretch"
        spacing={0}
        className="doc-view-metadata"
      >
        <Accordion expanded={false} className="doc-view-metadata-doc-id">
          <AccordionSummary
            expandIcon=""
            aria-controls="panel-docid-content"
            id="panel-docid-header"
          >
            <Typography component="div" variant="body1">
              <Grid container spacing={2} alignItems="center">
                <Grid item><b>Doc Id:</b></Grid>
                <Grid>{docIdLink(xhrHandler, logger, cdr, tenantId)}</Grid>
              </Grid>
            </Typography>
          </AccordionSummary>
          <AccordionDetails />
        </Accordion>
        {labelsElement}
        <DartAccordion
          id="pubInfo"
          title="Publication Information"
          className="doc-view-metadata-pub-info"
        >
          <Grid container direction="row" spacing={1}>
            {mLine('Title', title(cdr))}
            {mLine('Author', author(cdr))}
            {mLine('Genre', genre(cdr))}
            {mLine('Publisher', publisher(cdr))}
            {mLine('Publication/Creation Date', pubDate(cdr))}
            {mLine('Document Type', docType(cdr))}
            {mLine('Subject', subject(cdr))}
            {mLine('Original Language', language(cdr))}
            {mLine('Pages', pages(cdr))}
            {mLine('Classification', classification(cdr))}
          </Grid>
        </DartAccordion>
        <DartAccordion
          id="fileInfo"
          title="File Information"
          className="doc-view-metadata-file-info"
        >
          <Grid container direction="row" spacing={1}>
            {mLine('Content Type', contType(cdr))}
            {wordCount ? mLine('Word Count', wordCount) : ''}
            {mLine('Producer', producer(cdr))}
            {mLine('Creator', creator(cdr))}
          </Grid>
        </DartAccordion>
        <DartAccordion
          id="ingestInfo"
          title="Ingestion Information"
          className="doc-view-metadata-ingest-info"
        >
          <Grid container direction="row" spacing={1}>
            {mLine('Team', team(cdr))}
            {mLine('Ingestion Timestamp', timestamp(cdr))}
            {mLine('Ingestion Source', capSrc(cdr))}
          </Grid>
        </DartAccordion>
        <DartAccordion
          id="facets"
          title="Facets"
          className="doc-view-metadata-facets"
        >
          <Grid container direction="row" spacing={1}>
            {facetsElements}
          </Grid>
        </DartAccordion>
        <DartAccordion
          id="aggregations"
          title="Aggregations"
          className="doc-view-metadata-aggs"
        >
          <Grid container direction="row" spacing={1}>
            {aggsElements}
          </Grid>
        </DartAccordion>
      </Grid>
    );

    return (
      <StickyHeader header={header}>
        {body}
      </StickyHeader>
    );
  }
}

MetadataPanel.propTypes = {
  xhrHandler: PropTypes.func.isRequired,
  logger: PropTypes.shape({}).isRequired,
  tenantId: PropTypes.string.isRequired,
  cdr: PropTypes.shape({
    document_id: PropTypes.string.isRequired,
    annotations: PropTypes.arrayOf(
      PropTypes.shape({}),
    ).isRequired,
  }),
  aggregations: PropTypes.shape({}).isRequired,
  wordCount: PropTypes.number,
  docId: PropTypes.string.isRequired,
  classes: PropTypes.shape({
    bodyPaper: PropTypes.string.isRequired,
    accordionClosed: PropTypes.string.isRequired,
    accordionExpanded: PropTypes.string.isRequired,
  }).isRequired,
};

MetadataPanel.defaultProps = {
  wordCount: null,
  cdr: null,
};

function mapStateToProps(state, dartContext) {
  return {
    tenantId: state.dart.nav.tenantId,
    cdr: state.corpex.documentView.root.cdr,
    aggregations: state.corpex.documentView.root.aggregations,
    wordCount: state.corpex.documentView.root.wordCount,
    xhrHandler: dartContext.xhrHandler,
    logger: dartContext.log,
  };
}

export default connect(mapStateToProps)(withStyles(styles)(MetadataPanel));
