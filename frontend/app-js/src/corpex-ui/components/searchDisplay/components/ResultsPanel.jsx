import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { connect } from 'react-redux';
import withStyles from '@material-ui/core/styles/withStyles';
import Tab from '@material-ui/core/Tab';
import Tabs from '@material-ui/core/Tabs';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import CorpusViewer from '../../corpusView/components/CorpusViewer';
import SearchResults from '../../searchResults/components/SearchResults';
import StickyHeader from '../../../../common/components/layout/StickyHeader';
import { changeResultsView } from '../searchDisplay.actions';

const styles = () => ({
  root: {
    height: '100%',
    overflowY: 'scroll',
  },
  tabsWrapper: {
    flexGrow: 1,
    maxWidth: '100%',
  },
  tabMenu: {
    position: 'relative',
    flexGrow: 1,
  },
  count: {
    position: 'absolute',
    top: '50%',
    left: 15,
    '-ms-transform': 'translateY(-50%)',
    transform: 'translateY(-50%)',
  },
});

function a11yProps(index) {
  return {
    id: `scrollable-auto-tab-${index}`,
    'aria-controls': `scrollable-auto-tabpanel-${index}`,
  };
}

class ResultsPanel extends Component {
  render() {
    const {
      count,
      browseResults,
      dispatch,
      classes,
    } = this.props;

    const resultsViewerComponent = browseResults ? <SearchResults /> : <CorpusViewer />;

    const tabHandler = (e, v) => {
      dispatch(changeResultsView(v === 1));
    };

    const countOutput = count === null ? ''
      : <Typography variant="h6" color="textPrimary">{`${count} documents`}</Typography>;

    const resultsHeader = (
      <div className={classes.tabsWrapper}>
        <Paper square className={classes.tabMenu}>
          <div className={classes.count}>
            <Grid className="search-results-count" container dir="row" alignItems="center">
              {countOutput}
            </Grid>
          </div>
          <Tabs
            value={browseResults ? 1 : 0}
            onChange={tabHandler}
            indicatorColor="primary"
            textColor="primary"
            variant="standard"
            scrollButtons="auto"
            aria-label="Tag Type"
            centered
          >
            <Tab label="Corpus Overview" value={0} {...a11yProps(0)} />
            <Tab label="Browse Results" value={1} {...a11yProps(1)} />
          </Tabs>
        </Paper>
      </div>
    );

    return (
      <StickyHeader
        header={resultsHeader}
      >
        {resultsViewerComponent}
      </StickyHeader>
    );
  }
}

ResultsPanel.propTypes = {
  count: PropTypes.number,
  browseResults: PropTypes.bool.isRequired,
  dispatch: PropTypes.func.isRequired,
  classes: PropTypes.shape({
    tabMenu: PropTypes.string.isRequired,
    tabsWrapper: PropTypes.string.isRequired,
    count: PropTypes.string.isRequired,
  }).isRequired,
};

ResultsPanel.defaultProps = {
  count: null,
};

function mapStateToProps(state) {
  return {
    count: state.corpex.searchResults.count,
    browseResults: state.corpex.searchDisplay.browseResults,
  };
}

export default connect(mapStateToProps)(withStyles(styles)(ResultsPanel));
