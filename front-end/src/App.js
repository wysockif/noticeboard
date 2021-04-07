import RegistrationPage from "./pages/RegistrationPage";
import { register } from './api/apiCalls';

function App() {
  const actions = {
    postRegister: register
  };

  return (
    <div className="App">
      <header className="App-header">
        <RegistrationPage actions={actions} />
      </header>
    </div>
  );
}

export default App;
