const styles = () => ({
  wrapper: {
    paddingLeft: 10,
    paddingRight: 20,
    width: '100%',
    overflow: 'auto',
    whiteSpace: 'nowrap',
  },
  leftSide: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-start',
    marginRight: 12,
    '& > label': {
      width: 100,
      overflow: 'hidden',
      textOverflow: 'ellipsis',
      textAlign: 'left',
    },
  },
  progressBar: {
    width: '100%',
    height: 15,
    backgroundColor: 'lightgray',
    marginBottom: 8,
    borderRadius: 20,
    '& > div': {
      height: 15,
      backgroundColor: 'lightgreen',
      borderRadius: 20,
    },
  },
  progressBarFailed: {
    width: '100%',
    height: 15,
    backgroundColor: 'lightgray',
    marginBottom: 8,
    borderRadius: 20,
    '& > div': {
      height: 15,
      backgroundColor: 'darkred',
      borderRadius: 20,
    },
  },
  percentage: {
    marginLeft: 12,
  },
});

export default styles;
