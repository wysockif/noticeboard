// import RegistrationPage from "./pages/RegistrationPage";
import { login } from './api/apiCalls';
import LoginPage from "./pages/LoginPage";

function App() {
  const actions = {
    // postRegister: register
    postLogin: login
  };

  return (
    <div className="App">
      <header className="App-header">
        {/* <RegistrationPage actions={actions} /> */}
        <LoginPage actions={actions}/>
      </header>
    </div>
  );
}

export default App;
