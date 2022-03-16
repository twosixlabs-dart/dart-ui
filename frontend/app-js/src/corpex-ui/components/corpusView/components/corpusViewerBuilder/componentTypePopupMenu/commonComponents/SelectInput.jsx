import React from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import InputLabel from '@material-ui/core/InputLabel';
import Select from '@material-ui/core/Select';
import Input from '@material-ui/core/Input';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';

const styles = () => ({
  root: {},
});

function SelectInput({
  label,
  supportedFields,
  value,
  onChange,
  classes,
}) {
  return (
    <div className={classes.root}>
      <FormControl>
        <InputLabel id="date-field-select-label">{label}</InputLabel>
        <Select
          labelId="date-field-select-label"
          id="date-field-select"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          input={<Input />}
          className="date-search-field-select"
        >
          {supportedFields.map(([fieldId, fieldLabel]) => (
            <MenuItem key={fieldId} value={fieldId}>
              {fieldLabel}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </div>
  );
}

SelectInput.propTypes = {
  label: PropTypes.string.isRequired,
  supportedFields: PropTypes.arrayOf(PropTypes.arrayOf(PropTypes.string)).isRequired,
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
  classes: PropTypes.arrayOf(PropTypes.string).isRequired,
};

SelectInput.defaultProps = {
  value: '',
};

export default withStyles(styles)(SelectInput);
