import React, { Component } from 'react';
import PropTypes from 'prop-types';

// import { Redirect } from 'react-router-dom';

import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';
import ArrowForwardIosIcon from '@material-ui/icons/ArrowForwardIos';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ExpandLessIcon from '@material-ui/icons/ExpandLess';
import Button from '@material-ui/core/Button';
import { toggleSearchResultExpansion } from '../searchResults.actions';
import getCdrAggregations from '../thunk/getCdrAggregations.thunk';
import getCdrFacets from '../thunk/getCdrFacets.thunk';
import { docIdLink, sourceUri } from '../../../utilities/cdrReader';
import { connect } from '../../../../dart-ui/context/CustomConnect';

const styles = {
  root: {
  },
  paper: {
    padding: 10,
  },
  floatRight: {
    display: 'inline-block',
    float: 'right',
  },
  moreButton: {
    width: '100%',
    height: 20,
    marginTop: 5,
  },
};

class SearchResultsResult extends Component {
  render() {
    const {
      documentView,
      result,
      index,
      dispatch,
      classes,
      tenantId,
      xhrHandler,
      logger,
    } = this.props;

    const expandToggler = () => {
      if (result.aggregations === undefined || result.aggregations === null) {
        dispatch(getCdrAggregations(xhrHandler, index, result.cdr.document_id));
      }
      if (result.facets === undefined || result.facets === null) {
        dispatch(getCdrFacets(xhrHandler, index, result.cdr.document_id));
      }
      dispatch(toggleSearchResultExpansion(index));
    };

    // const docLinkClickHandler = e => {
    //     e.stopPropagation();
    //     e.nativeEvent.stopImmediatePropagation();
    //     window.open(`/documents/${result.cdr.document_id}`, '_blank');
    // };

    const openDocHandler = (e) => {
      e.stopPropagation();
      e.nativeEvent.stopImmediatePropagation();
      documentView(result.cdr.document_id);
    };

    const openDocElement = (
      <div className={classes.floatRight}>
        <Grid container direction="column" alignItems="flex-end">
          <Grid item xs={12}>
            <Button>
              <Grid
                container
                direction="row"
                justifyContent="flex-end"
                onClick={openDocHandler}
                className="view-document-button"
              >
                <Typography
                  component="div"
                  variant="subtitle1"
                  color="primary"
                >
                  <b>VIEW</b>
                </Typography>
                <ArrowForwardIosIcon color="primary" />
              </Grid>
            </Button>
          </Grid>
        </Grid>
      </div>
    );

    const cdrTest = result.cdr !== null
      && result.cdr !== undefined;
    const docIdTest = cdrTest
      && result.cdr.document_id !== null
      && result.cdr.document_id !== undefined;
    const extrMetaTest = cdrTest
      && result.cdr.extracted_metadata !== null
      && result.cdr.extracted_metadata !== undefined;
    const titleTest = extrMetaTest
      && result.cdr.extracted_metadata.Title !== null
      && result.cdr.extracted_metadata.Title !== undefined;
    const pubDateTest = extrMetaTest
      && result.cdr.extracted_metadata.CreationDate !== null
      && result.cdr.extracted_metadata.CreationDate !== undefined;
    const captSrcTest = cdrTest
      && result.cdr.capture_source !== null
      && result.cdr.capture_source !== undefined;
    const contTypeTest = cdrTest
      && result.cdr.content_type !== null
      && result.cdr.content_type !== undefined;
    const docTypeTest = extrMetaTest
      && result.cdr.extracted_metadata.Type !== null
      && result.cdr.extracted_metadata.Type !== undefined;
    const descTest = extrMetaTest
      && result.cdr.extracted_metadata.Description !== null
      && result.cdr.extracted_metadata.Description !== undefined;
    const description = descTest ? result.cdr.extracted_metadata.Description : '';

    const titleIsNotEmpty = titleTest && result.cdr.extracted_metadata.Title.trim() !== '';
    let filenameTitle = (cdrTest ? sourceUri(result.cdr) : '') || 'No Title';
    const filenameTitleSplit = filenameTitle.split('/');
    if (filenameTitleSplit.length > 1) {
      filenameTitle = filenameTitleSplit[filenameTitleSplit.length - 1];
    }
    const title = titleIsNotEmpty
      ? result.cdr.extracted_metadata.Title.replace(/\n/g, ' ').trim() : filenameTitle;
    const shortTitle = title.length < 60 ? title : `${title.slice(0, 60)} ...`;
    const shortDesc = description.length < 175 ? description : `${description.slice(0, 175)} ...`;
    const pubDate = pubDateTest ? result.cdr.extracted_metadata.CreationDate : '';
    const capSrc = captSrcTest ? (result.cdr.capture_source) : '';
    const contType = contTypeTest ? (result.cdr.content_type) : '';
    const docType = docTypeTest ? (result.cdr.extracted_metadata.Type) : '';
    const docId = docIdTest
      ? docIdLink(xhrHandler, logger, result.cdr, tenantId) : '';

    if (!result.expanded) {
      return (
        // eslint-disable-next-line max-len
        // eslint-disable-next-line jsx-a11y/click-events-have-key-events,jsx-a11y/no-static-element-interactions
        <div className={`${classes.root} search-results-result`}>
          <Paper className={`search-results-result-unexpanded ${classes.paper}`}>
            <Grid container direction="row" spacing={1} alignItems="center">
              {openDocElement}
              <Grid item>
                <Typography component="span" className="search-results-result-title" variant="h6">{shortTitle}</Typography>
              </Grid>
              <Grid item><Typography variant="subtitle1">-</Typography></Grid>
              { pubDateTest ? (
                <Grid item>
                  <Typography variant="subtitle1">
                    Published
                    {pubDate}
                  </Typography>
                </Grid>
              ) : ''}
              { captSrcTest ? <Grid item><Typography variant="subtitle1">-</Typography></Grid> : ''}
              { captSrcTest ? <Grid item><Typography variant="subtitle1">{capSrc}</Typography></Grid> : ''}
              { contTypeTest ? <Grid item><Typography variant="subtitle1">-</Typography></Grid> : ''}
              { contTypeTest ? <Grid item><Typography variant="subtitle1">{contType}</Typography></Grid> : ''}
              { docTypeTest ? <Grid item><Typography variant="subtitle1">-</Typography></Grid> : ''}
              { docTypeTest ? <Grid item><Typography variant="subtitle1">{docType}</Typography></Grid> : ''}
              { docIdTest ? <Grid item><Typography variant="subtitle1">-</Typography></Grid> : ''}
              { docIdTest ? <Grid item><Typography variant="subtitle1">{docId}</Typography></Grid> : ''}
              { descTest ? <Grid item xs={12}><Typography variant="body2"><em>{shortDesc}</em></Typography></Grid> : ''}
            </Grid>
            <Grid container direction="column">
              <Grid item xs={12}>
                <Button onClick={expandToggler} classes={{ root: classes.moreButton }}>
                  <ExpandMoreIcon />
                </Button>
              </Grid>
            </Grid>
          </Paper>
        </div>
      );
    }

    const creatorTest = extrMetaTest
      && result.cdr.extracted_metadata.Creator !== null
      && result.cdr.extracted_metadata.Creator !== undefined;
    const creator = creatorTest
      ? result.cdr.extracted_metadata.Creator : '';
    const authTest = extrMetaTest
      && result.cdr.extracted_metadata.Author !== null
      && result.cdr.extracted_metadata.Author !== undefined;
    const author = authTest ? result.cdr.extracted_metadata.Author : '';
    const producerTest = extrMetaTest
      && result.cdr.extracted_metadata.Producer !== null
      && result.cdr.extracted_metadata.Producer !== undefined;
    const producer = producerTest
      ? result.cdr.extracted_metadata.Producer : '';
    const subjTest = extrMetaTest
      && result.cdr.extracted_metadata.Subject !== null
      && result.cdr.extracted_metadata.Subject !== undefined;
    const subject = subjTest
      ? result.cdr.extracted_metadata.Subject : '';
    const classTest = extrMetaTest
      && result.cdr.extracted_metadata.Classification !== null
      && result.cdr.extracted_metadata.Classification !== undefined;
    const classification = classTest
      ? result.cdr.extracted_metadata.Classification : '';
    const langTest = extrMetaTest
      && result.cdr.extracted_metadata.OriginalLanguage !== null
      && result.cdr.extracted_metadata.OriginalLanguage !== undefined;
    const language = langTest
      ? result.cdr.extracted_metadata.OriginalLanguage : '';
    const pubTest = extrMetaTest
      && result.cdr.extracted_metadata.Publisher !== null
      && result.cdr.extracted_metadata.Publisher !== undefined;
    const publisher = pubTest
      ? result.cdr.extracted_metadata.Publisher : '';
    const tsTest = cdrTest
      && result.cdr.timestamp !== null
      && result.cdr.timestamp !== undefined;
    const timestamp = tsTest
      ? result.cdr.timestamp : '';
    const teamTest = cdrTest
      && result.cdr.team !== null
      && result.cdr.team !== undefined;
    const team = teamTest ? result.cdr.team : '';
    const facetsTest = result.facets !== undefined && result.facets !== null;
    const aggTest = result.aggregations !== undefined && result.aggregations !== null;
    const wcTest = result.word_count !== undefined && result.word_count !== null;
    const wordCount = wcTest ? `${result.word_count} words` : '';
    const pgTest = extrMetaTest
      && result.cdr.extracted_metadata.Pages !== null
      && result.cdr.extracted_metadata.Pages !== undefined;
    const pages = pgTest ? `${result.cdr.extracted_metadata.Pages} pages` : '';

    if (facetsTest) {
      Object.keys(result.facets).forEach((facetLabel) => {
        result.facets[facetLabel] = result.facets[facetLabel]
          .filter((facet) => facet.score === undefined
            || facet.score === null
            || facet.score >= 0.01);

        if (result.facets[facetLabel].length === 0) {
          Reflect.deleteProperty(result.facets, facetLabel);
        }
      });
    }

    const makeFacetsStr = (facets) => facets
      .sort((a, b) => {
        let res = -1;
        if (a.score !== null
          && a.score !== undefined
          && b.score !== null
          && b.score !== undefined) {
          res = a.score < b.score ? 1 : -1;
        } else if (a.value > b.value) res = 1;

        return res;
      }).map((facet) => {
        if (facet.score !== null && facet.score !== undefined) return `${facet.value} (${facet.score.toFixed(2)})`;
        return facet.value;
      })
      .join(', ');

    const makeAggStr = (aggs) => aggs
      .map((agg) => `${agg.value} (${agg.count})`)
      .join(', ');

    return (
      // eslint-disable-next-line max-len
      // eslint-disable-next-line jsx-a11y/click-events-have-key-events,jsx-a11y/no-static-element-interactions
      <div className={`${classes.root} search-results-result`}>
        <Paper className={`search-results-result-expanded ${classes.paper}`}>
          <Grid container spacing={1}>
            {openDocElement}
            <Grid item xs={12}>
              <Grid container direction="row" spacing={1} alignItems="flex-end">
                <Grid item><Typography className="search-results-result-title" variant="h6">{title}</Typography></Grid>
              </Grid>
            </Grid>
            <Grid item xs={12}>
              <Grid container direction="row" spacing={1} alignItems="flex-end">
                { pubDateTest ? (
                  <Grid item>
                    <Typography variant="subtitle1">
                      Published
                      {pubDate}
                    </Typography>
                  </Grid>
                ) : ''}
                { pubDateTest ? <Grid item><Typography variant="subtitle1">-</Typography></Grid> : ''}
                <Grid item><Typography variant="subtitle1">{authTest ? `Author: ${author}` : 'Unknown Author'}</Typography></Grid>
                { pubTest ? <Grid item><Typography variant="subtitle1">-</Typography></Grid> : ''}
                { pubTest ? <Grid item><Typography variant="subtitle1">{publisher}</Typography></Grid> : ''}
                { subjTest ? <Grid item><Typography variant="subtitle1">-</Typography></Grid> : ''}
                { subjTest ? <Grid item><Typography variant="subtitle1">{subject}</Typography></Grid> : ''}
              </Grid>
            </Grid>
            <Grid item xs={12}>
              <Grid container direction="row" spacing={1} alignItems="flex-end">
                <Grid item><Typography variant="subtitle1"><span style={{ fontWeight: 'bold' }}>Document Profile:</span></Typography></Grid>
                <Grid item><Typography variant="subtitle1">{docId}</Typography></Grid>
                <Grid item><Typography variant="subtitle1">-</Typography></Grid>
                <Grid item><Typography variant="subtitle1">{contType}</Typography></Grid>
                { docTypeTest ? <Grid item><Typography variant="subtitle1">-</Typography></Grid> : ''}
                { docTypeTest ? <Grid item><Typography variant="subtitle1">{docType}</Typography></Grid> : ''}
                { creatorTest ? <Grid item><Typography variant="subtitle1">-</Typography></Grid> : ''}
                { creatorTest ? <Grid item><Typography variant="subtitle1">{creator}</Typography></Grid> : ''}
                { producerTest ? <Grid item><Typography variant="subtitle1">{creatorTest ? '/' : '-'}</Typography></Grid> : ''}
                { producerTest ? <Grid item><Typography variant="subtitle1">{producer}</Typography></Grid> : ''}
                { langTest ? <Grid item><Typography variant="subtitle1">-</Typography></Grid> : ''}
                { langTest ? <Grid item><Typography variant="subtitle1">{language}</Typography></Grid> : ''}
                { classTest ? <Grid item><Typography variant="subtitle1">-</Typography></Grid> : ''}
                { classTest ? <Grid item><Typography variant="subtitle1">{classification}</Typography></Grid> : ''}
                { wcTest ? <Grid item><Typography variant="subtitle1">-</Typography></Grid> : ''}
                { wcTest ? <Grid item><Typography variant="subtitle1">{wordCount}</Typography></Grid> : ''}
                { pgTest ? <Grid item><Typography variant="subtitle1">{wcTest ? '/' : '-'}</Typography></Grid> : ''}
                { pgTest ? <Grid item><Typography variant="subtitle1">{pages}</Typography></Grid> : ''}
              </Grid>
            </Grid>
            <Grid item xs={12}>
              <Grid container direction="row" spacing={1} alignItems="flex-end">
                <Grid item><Typography variant="subtitle1"><span style={{ fontWeight: 'bold' }}>Ingestion Profile:</span></Typography></Grid>
                <Grid item><Typography variant="subtitle1">{team}</Typography></Grid>
                { captSrcTest ? <Grid item><Typography variant="subtitle1">-</Typography></Grid> : ''}
                { captSrcTest ? <Grid item><Typography variant="subtitle1">{capSrc}</Typography></Grid> : ''}
                { tsTest ? <Grid item><Typography variant="subtitle1">-</Typography></Grid> : ''}
                { tsTest ? <Grid item><Typography variant="subtitle1">{timestamp}</Typography></Grid> : ''}
              </Grid>
            </Grid>
            { facetsTest ? Object.keys(result.facets).map((facetLabel) => {
              const facets = result.facets[facetLabel];
              return (
                <Grid item xs={12} key={facetLabel}>
                  <Grid container direction="row">
                    <Grid item>
                      <Typography variant="subtitle1">
                        <span style={{ fontWeight: 'bold' }}>
                          {facetLabel}
                          :
                          {' '}
                        </span>
                        { makeFacetsStr(facets) }
                      </Typography>
                    </Grid>
                  </Grid>
                </Grid>
              );
            }) : '' }
            { aggTest ? Object.keys(result.aggregations).map((aggLabel) => {
              const aggs = result.aggregations[aggLabel];
              return (
                <Grid item xs={12} key={aggLabel}>
                  <Grid container direction="row">
                    <Grid item>
                      <Typography variant="subtitle1">
                        <span style={{ fontWeight: 'bold' }}>
                          {aggLabel}
                          :
                          {' '}
                        </span>
                        { makeAggStr(aggs) }
                      </Typography>
                    </Grid>
                  </Grid>
                </Grid>
              );
            }) : '' }
            { descTest ? (
              <Grid item xs={12}>
                <Typography variant="subtitle1"><span style={{ fontWeight: 'bold' }}>Description</span></Typography>
                <Typography variant="body1">{description}</Typography>
              </Grid>
            ) : ''}
          </Grid>
          <Grid container direction="column">
            <Grid item xs={12}>
              <Button onClick={expandToggler} classes={{ root: classes.moreButton }}>
                <ExpandLessIcon />
              </Button>
            </Grid>
          </Grid>
        </Paper>
      </div>
    );
  }
}

SearchResultsResult.propTypes = {
  logger: PropTypes.shape({}).isRequired,
  documentView: PropTypes.func.isRequired,
  tenantId: PropTypes.string.isRequired,
  classes: PropTypes.shape({
    floatRight: PropTypes.string.isRequired,
    root: PropTypes.string.isRequired,
    paper: PropTypes.string.isRequired,
    moreButton: PropTypes.string.isRequired,
  }).isRequired,
  result: PropTypes.shape({
    facets: PropTypes.objectOf(PropTypes.arrayOf(PropTypes.shape({}))),
    expanded: PropTypes.bool,
    aggregations: PropTypes.objectOf(PropTypes.arrayOf(PropTypes.shape({}))),
    word_count: PropTypes.number,
    cdr: PropTypes.shape({
      document_id: PropTypes.string.isRequired,
      extracted_metadata: PropTypes.shape({
        Title: PropTypes.string,
        Description: PropTypes.string,
        CreationDate: PropTypes.string,
        Publisher: PropTypes.string,
        Type: PropTypes.string,
        Creator: PropTypes.string,
        Author: PropTypes.string,
        Producer: PropTypes.string,
        Subject: PropTypes.string,
        Classification: PropTypes.string,
        OriginalLanguage: PropTypes.string,
        Pages: PropTypes.number,
      }),
      capture_source: PropTypes.string,
      content_type: PropTypes.string,
      timestamp: PropTypes.string,
      team: PropTypes.string,
    }).isRequired,
  }).isRequired,
  index: PropTypes.number.isRequired,
  dispatch: PropTypes.func.isRequired,
  xhrHandler: PropTypes.func.isRequired,
};

// SearchResultsResult.defaultProps = {
//
// };

const mapStateToProps = (state, dartContext) => ({
  documentView: dartContext.router.documentView,
  xhrHandler: dartContext.xhrHandler,
  logger: dartContext.log,
});

export default connect(mapStateToProps)(withStyles(styles)(SearchResultsResult));
