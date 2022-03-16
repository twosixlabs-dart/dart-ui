import React from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import PlusIcon from '@material-ui/icons/Add';
import IconButton from '@material-ui/core/IconButton';
import { Menu, MenuItem, Modal } from '@material-ui/core';
import CorpusViewerComponentPopupMenu from './componentTypePopupMenu/CorpusViewerComponentPopupMenu';
import { componentData } from '../../componentData/corpusViewUserComponentTypes';

import { connect } from '../../../../../dart-ui/context/CustomConnect';

const styles = () => ({
});

function CorpusViewerBuilderAddComponentMenuItem({
  supportedComponentTypes,
  sectionId,
  addComponent,
}) {
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [modalState, setModalState] = React.useState({ componentType: null, open: false });

  const handleModalClose = () => {
    setModalState({ componentType: null, open: false });
  };

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleModalOpen = (componentType) => () => {
    handleClose();
    setModalState({ componentType, open: true });
  };

  return (
    <div>
      <IconButton
        edge="start"
        color="primary"
        aria-label="menu"
        aria-controls="add-component-menu"
        aria-haspopup="true"
        onClick={handleClick}
        className="dart-ui-navbar-hamburger-button"
      >
        <PlusIcon />
      </IconButton>
      <Menu
        id="add-component-menu"
        anchorEl={anchorEl}
        keepMounted
        open={Boolean(anchorEl)}
        onClose={handleClose}
      >
        {supportedComponentTypes.map((componentType) => (
          <MenuItem
            onClick={handleModalOpen(componentType)}
          >
            {Object.prototype.hasOwnProperty.call(componentData, componentType) ? componentData[componentType].label : '------'}
          </MenuItem>
        ))}
      </Menu>
      <Modal
        open={modalState.open}
        onClose={handleModalClose}
        aria-labelledby="Add Component"
        aria-describedby="Add a new component"
      >
        <CorpusViewerComponentPopupMenu
          componentType={modalState.componentType}
          closePopup={handleModalClose}
          sectionId={sectionId}
          addComponent={addComponent}
        />
      </Modal>
    </div>
  );
}

CorpusViewerBuilderAddComponentMenuItem.propTypes = {
  supportedComponentTypes: PropTypes.arrayOf(PropTypes.string).isRequired,
  sectionId: PropTypes.oneOf([undefined, PropTypes.string]).isRequired,
  addComponent: PropTypes.func.isRequired,
};

function mapStateToProps() {
  return {
  };
}

export default connect(mapStateToProps)(withStyles(styles)(
  CorpusViewerBuilderAddComponentMenuItem,
));
