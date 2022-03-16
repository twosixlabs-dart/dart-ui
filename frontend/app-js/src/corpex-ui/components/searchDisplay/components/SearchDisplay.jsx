import React, { Component } from 'react';
import { withStyles } from '@material-ui/core';
import TwoPanel from '../../../../common/components/layout/TwoPanel';
import SearchPanel from './SearchPanel';
import ResultsPanel from './ResultsPanel';

const styles = () => ({
});

class SearchDisplay extends Component {
  render() {
    return (
      <TwoPanel
        independentScroll
        left={<SearchPanel />}
        right={<ResultsPanel />}
      />
    );
  }
}

export default withStyles(styles)(SearchDisplay);
