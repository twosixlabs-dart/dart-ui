import { combineReducers } from 'redux';
import corpexCorpusViewReducer from './components/corpusView/corpusView.reducer';
import corpexDocumentViewReducer from './components/documentView/documentView.reducer';
import corpexSearchBuilderReducer from './components/searchBuilder/searchBuilder.reducer';
import corpexSearchResultsReducer from './components/searchResults/searchResults.reducer';
import corpexSearchDisplayReducer from './components/searchDisplay/searchDisplay.reducer';
import corpexCorpexRootReducer from './components/corpexRoot/corpexRoot.reducer';

export default combineReducers({
  corpusView: corpexCorpusViewReducer,
  documentView: corpexDocumentViewReducer,
  searchBuilder: corpexSearchBuilderReducer,
  searchResults: corpexSearchResultsReducer,
  searchDisplay: corpexSearchDisplayReducer,
  corpexRoot: corpexCorpexRootReducer,
});
