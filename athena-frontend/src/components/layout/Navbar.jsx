export default function Navbar() {
  return (
    <nav className="fixed top-0 w-full z-50 backdrop-blur-md bg-black/40 border-b border-white/5">
      <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">

        {/* Logo */}
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded bg-gradient-to-br from-red-600 to-orange-600 flex items-center justify-center font-bold text-white shadow-[0_0_15px_rgba(220,38,38,0.5)]">
            A
          </div>
          <span className="font-display font-bold text-xl tracking-wider">
            ATHENA
          </span>
        </div>

        {/* Links */}
        <div className="hidden md:flex gap-8 text-sm font-medium text-gray-400">
          <a href="#hero" className="hover:text-red-400 transition-colors">
            Mission
          </a>
          <a href="#dashboard" className="hover:text-red-400 transition-colors">
            Console
          </a>
          <a href="#metrics" className="hover:text-red-400 transition-colors">
            Metrics
          </a>
        </div>

        {/* Status */}
        <div className="flex items-center gap-2">
          <span className="w-2 h-2 rounded-full bg-green-500 animate-pulse" />
          <span className="text-xs font-mono text-green-400">
            SYSTEM ACTIVE
          </span>
        </div>
      </div>
    </nav>
  );
}
