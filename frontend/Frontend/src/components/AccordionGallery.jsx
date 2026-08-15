import { useState } from 'react'

export default function AccordionGallery({ items = [] }) {
  const [activeIndex, setActiveIndex] = useState(0)

  return (
    <div className="flex h-72 w-full gap-2 overflow-hidden rounded-xl sm:h-80">
      {items.map((item, index) => (
        <a
          key={item.label}
          href={item.link || '#'}
          onMouseEnter={() => setActiveIndex(index)}
          onFocus={() => setActiveIndex(index)}
          className={`group relative overflow-hidden rounded-xl transition-all duration-500 ${
            activeIndex === index
              ? 'flex-[4]'
              : 'flex-1'
          }`}
        >
          <img
            src={item.image}
            alt={item.label}
            className="absolute inset-0 h-full w-full object-cover transition-transform duration-500 group-hover:scale-105"
          />

          <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent" />

          <div className="absolute bottom-4 left-4">
            <p className="text-sm font-bold text-white sm:text-base">
              {item.label}
            </p>
          </div>
        </a>
      ))}
    </div>
  )
}