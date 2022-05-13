/* eslint-disable import/no-cycle */

import React, { Component, Suspense } from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import Typography from '@material-ui/core/Typography';
import InputLabel from '@material-ui/core/InputLabel';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import Paper from '@material-ui/core/Paper';
import IconButton from '@material-ui/core/IconButton';
import CloseIcon from '@material-ui/icons/Close';
import { boolTypes, componentTypes } from '../searchComponentData/enums';
import AbsoluteCentered from '../../../../common/components/layout/AbsoluteCentered';
import { connect } from '../../../../dart-ui/context/CustomConnect';
import SearchBuilder from './SearchBuilder';

const QueryStringSearch = React.lazy(() => import(/* webpackChunkName: "queryStringSearch" */ './queryStringSearch/QueryStringSearch'));
const DateSearch = React.lazy(() => import(/* webpackChunkName: "dateSearch" */ './dateSearch/DateSearch'));
const IntegerSearch = React.lazy(() => import(/* webpackChunkName: "integerSearch" */ './integerSearch/IntegerSearch'));
const TagSearch = React.lazy(() => import(/* webpackChunkName: "tagSearch" */ './tagSearch/TagSearch'));
const TermSearch = React.lazy(() => import(/* webpackChunkName: "termSearch" */ './termSearch/TermSearch'));
const FacetSearch = React.lazy(() => import(/* webpackChunkName: "facetSearch" */ './facetSearch/FacetSearch'));

const styles = () => ({
  root: {
    padding: 10,
    position: 'relative',
  },
  titleSummaryWrapper: {
    float: 'left',
    maxWidth: 'calc(100% - 165px)',
    overflow: 'hidden',
    whiteSpace: 'nowrap',
  },
  rightMenu: {
    float: 'right',
  },
  clearDiv: {
    clear: 'both',
  },
  closeButton: {
    marginLeft: 5,
    // position: "absolute",
    // top: 0,
    // right: 0,
  },
  componentHeader: {
    position: 'relative',
    overflow: 'auto',
    '&::after': {
      content: '',
      clear: 'both',
      display: 'table',
    },
    '&:hover': {
      cursor: 'pointer',
    },
  },
  componentContent: {
  },
  summary: {
    padding: 15,
    paddingTop: 0,
  },
});

class SearchComponent extends Component {
  render() {
    const {
      componentId,
      type,
      boolType,
      privateAggQueries,
      commonAggQueries,
      isEdited,
      isActive,
      summary,
      title,
      componentState,
      getPrivateAggs,
      executePrivateAggQueries,
      updateCallback,
      boolTypeCallback,
      getAggResults,
      toggleEditedCallback,
      searchBuilderCallback,
      removeComponentCallback,
      componentIndex,
      classes,
      loader,
    } = this.props;

    const childProps = type === componentTypes.BOOL_SEARCH ? {
      boolType,
      updateCallback,
      removeComponentCallback,
      searchBuilderCallback,
      executePrivateAggQueries,
      commonAggQueries,
      privateAggQueries,
      componentIndex,
      componentState,
    } : {
      boolType,
      componentId,
      updateCallback,
      privateAggQueries,
      commonAggQueries,
      getPrivateAggs,
      getAggResults,
      removeComponentCallback,
      componentState,
    };

    const boolTypeHandler = (e) => {
      e.preventDefault();
      boolTypeCallback(e.target.value);
    };

    const componentHeader = (
      // eslint-disable-next-line max-len
      // eslint-disable-next-line jsx-a11y/click-events-have-key-events,jsx-a11y/no-static-element-interactions
      <div
        onClick={(e) => {
          if (e.target.tagName === 'DIV' || e.target.tagName === 'SPAN') toggleEditedCallback();
        }}
        className="search-builder-component-header"
      >
        <div className={classes.componentHeader}>
          <div className={classes.titleSummaryWrapper}>
            <Typography
              variant="h6"
              component="div"
              color="primary"
            >
              {title}
            </Typography>
            {isEdited || !isActive ? '' : (
              <Typography className="search-builder-component-summary" variant="body1" component="span">
                <b>{summary}</b>
              </Typography>
            )}
          </div>
          <div className={classes.rightMenu}>
            <FormControl>
              <InputLabel id={`bool-type-select-label-${componentId}`}>Behavior:</InputLabel>
              <Select
                labelId={`bool-type-select-label-${componentId}`}
                id={`bool-type-select-${componentId}`}
                value={boolType}
                onChange={boolTypeHandler}
                onClick={(e) => e.preventDefault()}
                className="search-component-select-bool-type"
              >

                <MenuItem value={boolTypes.SHOULD}>Prefer Match</MenuItem>
                <MenuItem value={boolTypes.MUST}>Require Match</MenuItem>
                <MenuItem value={boolTypes.MUST_NOT}>Exclude Match</MenuItem>
                <MenuItem value={boolTypes.FILTER}>Filter Search</MenuItem>
              </Select>
            </FormControl>
            <IconButton
              classes={{ sizeSmall: classes.closeButton }}
              aria-label="close"
              size="small"
              onClick={removeComponentCallback}
              className="search-component-close-button"
            >
              <CloseIcon fontSize="small" />
            </IconButton>
          </div>
          <div className={classes.clearDiv} />
        </div>
      </div>
    );

    let componentContent;
    if (!isEdited) componentContent = '';
    else {
      switch (type) {
        case componentTypes.QUERY_STRING_SEARCH: {
          componentContent = (
            <Suspense fallback={<AbsoluteCentered>{loader}</AbsoluteCentered>}>
              <QueryStringSearch {...childProps} />
            </Suspense>
          );
          break;
        }

        case componentTypes.DATE_SEARCH: {
          componentContent = (
            <Suspense fallback={<AbsoluteCentered>{loader}</AbsoluteCentered>}>
              <DateSearch {...childProps} />
            </Suspense>
          );
          break;
        }

        case componentTypes.TEXT_LENGTH_SEARCH: {
          componentContent = (
            <Suspense fallback={<AbsoluteCentered>{loader}</AbsoluteCentered>}>
              <IntegerSearch {...childProps} />
            </Suspense>
          );
          break;
        }

        case componentTypes.TERM_SEARCH: {
          componentContent = (
            <Suspense fallback={<AbsoluteCentered>{loader}</AbsoluteCentered>}>
              <TermSearch {...childProps} />
            </Suspense>
          );
          break;
        }

        case componentTypes.ENTITY_SEARCH:
        case componentTypes.EVENT_SEARCH: {
          componentContent = (
            <Suspense fallback={<AbsoluteCentered>{loader}</AbsoluteCentered>}>
              <TagSearch {...childProps} />
            </Suspense>
          );
          break;
        }

        case componentTypes.TOPIC_SEARCH:
        case componentTypes.FACTIVA_SEARCH: {
          componentContent = (
            <Suspense fallback={<AbsoluteCentered>{loader}</AbsoluteCentered>}>
              <FacetSearch {...childProps} />
            </Suspense>
          );
          break;
        }

        case componentTypes.BOOL_SEARCH: {
          // No need to make this lazy since this will already be inside a search builder
          componentContent = (
            <SearchBuilder {...childProps} />
          );
          break;
        }

        default: {
          componentContent = <div />;
        }
      }
    }

    return (
      <Paper className={`search-builder-component ${classes.root}`}>
        {componentHeader}
        <div className={`search-builder-component-content ${classes.componentContent}`}>
          {componentContent}
        </div>
      </Paper>
    );
  }
}

SearchComponent.propTypes = {
  componentId: PropTypes.string.isRequired,
  type: PropTypes.string.isRequired,
  boolType: PropTypes.string.isRequired,
  privateAggQueries: PropTypes.objectOf(PropTypes.shape({})).isRequired,
  commonAggQueries: PropTypes.objectOf(PropTypes.shape({})).isRequired,
  isEdited: PropTypes.bool.isRequired,
  isActive: PropTypes.bool.isRequired,
  summary: PropTypes.string.isRequired,
  title: PropTypes.string.isRequired,
  componentState: PropTypes.shape({}).isRequired,
  getPrivateAggs: PropTypes.func.isRequired,
  executePrivateAggQueries: PropTypes.func.isRequired,
  updateCallback: PropTypes.func.isRequired,
  boolTypeCallback: PropTypes.func.isRequired,
  getAggResults: PropTypes.func.isRequired,
  toggleEditedCallback: PropTypes.func.isRequired,
  searchBuilderCallback: PropTypes.func.isRequired,
  removeComponentCallback: PropTypes.func.isRequired,
  componentIndex: PropTypes.objectOf(PropTypes.shape({})).isRequired,
  classes: PropTypes.shape({
    componentHeader: PropTypes.string.isRequired,
    componentContent: PropTypes.string.isRequired,
    titleSummaryWrapper: PropTypes.string.isRequired,
    rightMenu: PropTypes.string.isRequired,
    closeButton: PropTypes.string.isRequired,
    clearDiv: PropTypes.string.isRequired,
    root: PropTypes.string.isRequired,
  }).isRequired,
  loader: PropTypes.node.isRequired,
};

const mapStateToProps = (_, dartContext) => ({
  loader: dartContext.loader,
});

export default connect(mapStateToProps)(withStyles(styles)(SearchComponent));
