import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { withStyles, Select } from '@material-ui/core';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';

import SearchBuilderRoot from '../../searchBuilder/components/SearchBuilderRoot';
import downloadData from '../../../../common/utilities/downloadData';
import { USE_DART_AUTH } from '../../../../common/config/constants';
import { connect } from '../../../../dart-ui/context/CustomConnect';

const styles = () => ({
  root: {
    height: '100%',
    overflowY: 'auto',
  },
});

class SearchPanel extends Component {
  render() {
    const {
      classes,
      browseResults,
      searchQueries,
      tenantId,
      tenants,
    } = this.props;

    const authDisabled = !USE_DART_AUTH;
    const tenantChoiceComponent = (authDisabled || tenants.length > 0) ? '' : (
      <Grid item>
        <Typography component="h2" variant="h6" color="textPrimary">
          Tenant:
        </Typography>
        <Select
          native
          value={tenantId}
          onChange={this.updateTenant}
          inputProps={{
            name: 'relevance',
            id: 'relevance-select-input',
          }}
        >
          {tenants
            .map((tenant) => (<option key={tenant} value={tenant}>{tenant}</option>))}
        </Select>
      </Grid>
    );

    return (
      <div className={`search-panel ${classes.root}`}>
        <Grid container spacing={4} alignItems="center" alignContent="center">
          {tenantChoiceComponent}
          <Grid item>
            <Button
              onClick={() => downloadData(JSON.stringify(searchQueries), 'search-queries.json', 'application/json')}
              disabled={!searchQueries || searchQueries.length === 0}
            >
              Export Search Query
            </Button>
          </Grid>
        </Grid>
        <SearchBuilderRoot getResults={browseResults} />
      </div>
    );
  }
}

SearchPanel.propTypes = {
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
  }).isRequired,
  browseResults: PropTypes.bool.isRequired,
  searchQueries: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  tenantId: PropTypes.string,
  tenants: PropTypes.arrayOf(PropTypes.string).isRequired,
};

SearchPanel.defaultProps = {
  tenantId: null,
};

function mapStateToProps(state, dartContext) {
  return {
    browseResults: state.corpex.searchDisplay.browseResults,
    searchQueries: state.corpex.searchResults.searchQueries,
    tenantId: state.dart.nav.tenantId,
    tenants: dartContext.tenants,
  };
}

export default connect(mapStateToProps)(withStyles(styles)(SearchPanel));
