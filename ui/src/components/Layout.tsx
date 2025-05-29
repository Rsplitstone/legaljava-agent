import { Outlet, NavLink } from 'react-router-dom';
import ChatPanel from '../sidebar/ChatPanel';
import clsx from 'clsx';

export default function Layout() {
  return (
    <div className="flex h-screen">
      {/* Left rail */}
      <aside className="w-60 border-r bg-gray-50 dark:bg-gray-900">
        <nav className="p-4 space-y-2">
          {['Schedule', 'Analytics'].map((p) => (
            <NavLink
              key={p}
              to={p === 'Schedule' ? '/' : '/' + p.toLowerCase()}
              className={({ isActive }) =>
                clsx(
                  'block rounded-lg px-3 py-2',
                  isActive ? 'bg-accent text-white' : 'hover:bg-gray-200 dark:hover:bg-gray-700'
                )
              }
            >
              {p}
            </NavLink>
          ))}
        </nav>
      </aside>

      {/* Main canvas */}
      <main className="flex-1 overflow-hidden">
        <Outlet />
      </main>

      {/* Right rail */}
      <ChatPanel />
    </div>
  );
}
