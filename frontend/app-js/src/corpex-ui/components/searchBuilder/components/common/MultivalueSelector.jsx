import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { withStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import DartTooltip from '../../../../../common/components/DartTooltip';

const styles = {
  tabsWrapper: {
    flexGrow: 1,
    maxWidth: '100%',
  },
  tabMenu: {
    flexGrow: 1,
  },
};

function a11yProps(index) {
  return {
    id: `scrollable-auto-tab-${index}`,
    'aria-controls': `scrollable-auto-tabpanel-${index}`,
  };
}

class MultivalueSelector extends Component {
  render() {
    const {
      classes,
      value,
      onChange,
      possibleValues,
    } = this.props;

    return (
      <div
        className={`multi-value-selector ${classes.tabsWrapper}`}
      >

        <Paper square className={classes.tabMenu}>
          <Tabs
            value={value || false}
            onChange={onChange}
            indicatorColor="primary"
            textColor="primary"
            variant="scrollable"
            scrollButtons="auto"
            aria-label="Term Field"
          >
            {possibleValues
              // eslint-disable-next-line arrow-body-style
              .map((valueElement, index) => {
                // eslint-disable-next-line react/jsx-props-no-spreading
                const label = valueElement.description ? (
                  <DartTooltip body={valueElement.description}>
                    <div>{valueElement.label}</div>
                  </DartTooltip>
                ) : valueElement.label;
                return (
                  <Tab
                    className="search-type-selector-type-tab"
                    label={label}
                    value={valueElement.value}
                    {...a11yProps(index)}
                    key={label}
                  />
                );
              })}
          </Tabs>
        </Paper>
      </div>
    );
  }
}

MultivalueSelector.propTypes = {
  // eslint-disable-next-line react/forbid-prop-types
  value: PropTypes.any,
  onChange: PropTypes.func.isRequired,
  possibleValues: PropTypes.arrayOf(PropTypes.shape({
    label: PropTypes.string.isRequired,
    // eslint-disable-next-line react/forbid-prop-types
    value: PropTypes.any.isRequired,
    description: PropTypes.string,
  })).isRequired,
  classes: PropTypes.shape({
    tabMenu: PropTypes.string.isRequired,
    tabsWrapper: PropTypes.string.isRequired,
  }).isRequired,
};

MultivalueSelector.defaultProps = {
  value: null,
};

export default withStyles(styles)(MultivalueSelector);
