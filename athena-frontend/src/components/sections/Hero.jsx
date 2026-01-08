export default function Hero() {
  return (
    <section
      id="hero"
      className="relative min-h-screen flex items-center justify-center pt-24 overflow-hidden"
    >
      {/* Glow */}
      <div className="absolute inset-0 flex items-center justify-center -z-10">
        <div className="w-[800px] h-[800px] bg-red-600/10 rounded-full blur-3xl animate-pulse-slow" />
      </div>

      <div className="text-center px-6 max-w-4xl">
        {/* Badge */}
        <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-red-900/20 border border-red-500/30 text-red-400 text-xs font-mono mb-8 animate-slide-up">
          <span className="w-2 h-2 bg-red-500 rounded-full animate-pulse" />
          AI-POWERED ANOMALY DETECTION
        </div>

        {/* Heading */}
        <h1 className="font-display text-5xl md:text-7xl font-bold leading-tight mb-6 animate-slide-up">
          Financial Security <br />
          <span className="bg-clip-text text-transparent bg-gradient-to-r from-red-500 via-orange-500 to-red-500">
            Reimagined.
          </span>
        </h1>

        {/* Subtitle */}
        <p className="text-gray-400 text-lg mb-10 animate-slide-up">
          Athena analyzes transactions in real time using machine learning
          to detect fraud before it settles.
        </p>

        {/* CTA */}
        <a
          href="#dashboard"
          className="inline-flex items-center gap-2 px-8 py-4 rounded-full font-bold bg-white text-black hover:scale-105 transition-transform relative overflow-hidden group"
        >
          <span className="absolute inset-0 bg-gradient-to-r from-red-500 to-orange-500 opacity-0 group-hover:opacity-100 transition-opacity" />
          <span className="relative z-10 group-hover:text-white">
            Launch Console
          </span>
          <span className="relative z-10 group-hover:text-white">↓</span>
        </a>
      </div>
    </section>
  );
}
