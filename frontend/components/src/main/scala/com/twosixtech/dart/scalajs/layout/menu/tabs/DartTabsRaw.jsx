import React, { Component } from 'react';
import {
  Paper,
  Tab,
  Tabs,
  withStyles,
} from '@material-ui/core';
import DartTabsProps from './DartTabsProps';

const styles = () => ({
  tabMenu: {
    position: 'relative',
    flexGrow: 1,
  },
});

const a11yProps = (index) => ({
  id: `scrollable-auto-tab-${index}`,
  'aria-controls': `scrollable-auto-tabpanel-${index}`,
});

class DartTabs extends Component {
  render() {
    const {
      onChange,
      value,
      tabs,
      classes,
    } = this.props;

    const changeHandler = (e, v) => onChange(v);

    return (
      <Paper square className={classes.tabMenu}>
        <Tabs
          value={value}
          onChange={changeHandler}
          indicatorColor="primary"
          textColor="primary"
          variant="standard"
          scrollButtons="auto"
          aria-label="Tag Type"
          centered
        >
          {tabs.map((tab, i) => (
            <Tab
              label={tab.label}
              value={tab.value}
              key={tab.value}
              {...a11yProps(i)}
            />
          ))}
        </Tabs>
      </Paper>
    );
  }
}

DartTabs.propTypes = DartTabsProps.propTypes;
DartTabs.defaultProps = DartTabsProps.defaultProps;

export default withStyles(styles)(DartTabs);
