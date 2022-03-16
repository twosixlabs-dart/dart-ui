import React, { Component } from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import CloseIcon from '@material-ui/icons/Close';
import AddCircleOutlineIcon from '@material-ui/icons/AddCircleOutline';
import IconButton from '@material-ui/core/IconButton';
import ListItemText from '@material-ui/core/ListItemText';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import { MenuList } from '@material-ui/core';
import MenuItem from '@material-ui/core/MenuItem';

import availableComponents from '../utilities/availableSearchComponents';
import bootStrapComponent from '../../../config/componentBootstrap';

const styles = (theme) => ({
  hiddenAdder: {
    paddingTop: theme.spacing(2),
  },
  componentList: {
    '& span': {
      '&:hover': {
        cursor: 'pointer',
      },
    },
  },
  marginBottom: {
    marginBottom: theme.spacing(1),
  },
});

class SearchComponent extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isHidden: !!props.inMiddle,
      isExpanded: false,
    };
  }

  render() {
    const {
      inMiddle,
      addComponentCallback,
      classes,
    } = this.props;

    const {
      isHidden,
      isExpanded,
    } = this.state;

    const hoverOnHandler = () => {
      this.setState((lastState) => ({
        ...lastState,
        isHidden: false,
      }));
    };

    const hoverOffHandler = () => {
      if (inMiddle) {
        this.setState((lastState) => ({
          ...lastState,
          isHidden: true,
        }));
      }
    };

    const expandAdderHandler = () => {
      this.setState((lastState) => ({
        ...lastState,
        isExpanded: true,
      }));
    };

    const closeAdderHandler = () => {
      this.setState((lastState) => ({
        ...lastState,
        isExpanded: false,
      }));
    };

    const addComponentHandler = (componentType) => () => {
      addComponentCallback(componentType);
      this.setState((lastState) => ({
        ...lastState,
        isExpanded: false,
        isHidden: true,
      }));
    };

    const hiddenAdder = (
      <Grid
        className={`search-builder-hidden-adder ${classes.hiddenAdder}`}
        container
        onMouseEnter={hoverOnHandler}
      />
    );

    const adderButton = (
      <Grid
        className={`search-builder-adder-button ${classes.marginBottom}`}
        container
        direction="column"
        justifyContent="center"
        alignItems="center"
        onMouseLeave={hoverOffHandler}
      >
        <IconButton
          aria-label="add"
          onClick={expandAdderHandler}
        >
          <AddCircleOutlineIcon fontSize="large" />
        </IconButton>
      </Grid>
    );

    const expandedAdderMenuItems = availableComponents.map((componentType) => (
      <MenuItem
        key={`available-component-${componentType}`}
        onClick={addComponentHandler(componentType)}
        className="search-builder-expanded-adder-component"
      >
        <ListItemText>
          {bootStrapComponent(componentType).title}
        </ListItemText>
      </MenuItem>
    ));

    const expandedAdder = (
      <Grid
        container
        direction="column"
        alignItems="stretch"
        className={`search-builder-expanded-adder ${classes.marginBottom}`}
      >
        <Paper className={classes.componentList}>
          <IconButton
            aria-label="close"
            size="small"
            onClick={closeAdderHandler}
            className="search-builder-expanded-adder-close-button"
          >
            <CloseIcon fontSize="small" />
          </IconButton>
          <MenuList>
            {expandedAdderMenuItems}
          </MenuList>
        </Paper>
      </Grid>
    );

    let adderElement = adderButton;
    if (isHidden) adderElement = hiddenAdder;
    else if (isExpanded) adderElement = expandedAdder;

    return (
      <Grid container justifyContent="center" spacing={1} className="search-builder-adder">
        {adderElement}
      </Grid>
    );
  }
}

SearchComponent.propTypes = {
  addComponentCallback: PropTypes.func.isRequired,
  inMiddle: PropTypes.bool,
  classes: PropTypes.shape({
    hiddenAdder: PropTypes.string.isRequired,
    marginBottom: PropTypes.string.isRequired,
    componentList: PropTypes.string.isRequired,
  }).isRequired,
};

SearchComponent.defaultProps = {
  inMiddle: false,
};

export default withStyles(styles)(SearchComponent);
